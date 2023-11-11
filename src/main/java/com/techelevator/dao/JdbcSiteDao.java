package com.techelevator.dao;

import com.techelevator.exception.DaoException;
import com.techelevator.model.Campground;
import com.techelevator.model.Park;
import com.techelevator.model.Site;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcSiteDao implements SiteDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcSiteDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesWithRVAccessByParkId(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT * FROM site JOIN campground ON site.campground_id = campground.campground_id " +
        "JOIN park ON campground.park_id = park.park_id " +
        "WHERE park.park_id = ? AND max_rv_length > 0;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
            while (results.next()) {
                Site resultSites = mapRowToSite(results);
                sites.add(resultSites);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return sites;
    }
//    CURRENT_DATE + 1, CURRENT_DATE + 2, CURRENT_DATE - 10
//    from_date has to be AFTER from_date >  CURRENT_DATE
//    to_date
//        NULL
    // if current_date is after from date - no
    // if current_date before, yes
        // to_date - from_date = days
        // current_date uses days to save time searching
    // NULL exceptions?
//  "WHERE park_id = ? AND CURRENT_DATE NOT BETWEEN from_date AND to_date;";
    //((from_date > CURRENT_DATE) AND (to_date < CURRENT_DATE)

    //  "WHERE park_id = ? AND (((from_date >= CURRENT_DATE) OR (from_date IS NULL)) AND ((CURRENT_DATE < to_date) OR (to_date IS NULL)));";
//   "WHERE park_id = ? AND CURRENT_DATE NOT BETWEEN from_date AND to_date;";
//  //                 11/8             11/10
//   "AND ((reservation.to_date < CURRENT_DATE AND reservation.from_date >= CURRENT_DATE) OR reservation.from_date IS NULL) "+
//    "WHERE campground.park_id = ? AND reservation.reservation_id IS NULL;";


    @Override
    public List<Site> getSitesWithoutReservationByParkId(int parkId) {
        List<Site> sites = new ArrayList<>();
            String sql = "SELECT DISTINCT site.campground_id, site.site_id, site.site_number, max_occupancy, accessible, max_rv_length, utilities FROM site " +
            "JOIN campground ON site.campground_id = campground.campground_id " +
            "JOIN reservation ON site.site_id = reservation.site_id " +
            "WHERE park_id = ? AND (reservation.to_date < CURRENT_DATE OR (reservation.to_date > CURRENT_DATE AND reservation.from_date > CURRENT_DATE));";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
            while (results.next()) {
                Site resultSites = mapRowToSite(results);
                sites.add(resultSites);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return sites;
    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
