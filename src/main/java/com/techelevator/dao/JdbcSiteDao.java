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

    @Override
    public List<Site> getSitesWithoutReservationByParkId(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT * FROM site JOIN campground ON site.campground_id = campground.campground_id " +
        "LEFT JOIN reservation ON site.site_id = reservation.site_id " +
        "WHERE park_id = ? start_date = ? AND end_date = ?;";
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
