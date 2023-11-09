package com.techelevator.dao;

import com.techelevator.model.Campground;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.List;

public interface CampgroundDao {


    Campground getCampgroundById(int id);
	/**
	 * Get a specific campground with the given id.
	 * If the id is not found, return null.
	 *
	 * @param id the id of the campground to retrieve
	 * @return a Campground object
	 */


    List<Campground> getCampgroundsByParkId(int parkId);
	/**
	 * Get all the campsites from a park with the given id.
	 * If the id is not found, return an empty List.
	 *
	 * @param parkId the id of the park to retrieve campgrounds from
	 * @return a List of Campground objects
	 */

//	@Override
//	public List<City> getCitiesByState(String stateAbbreviation) {
//		List<City> cities = new ArrayList<>();
//		String sql = "SELECT city_id, city_name, state_abbreviation, population, area " +
//				"FROM city " +
//				"WHERE state_abbreviation = ?;";
//		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, stateAbbreviation);
//		while (results.next()) {
//			cities.add(mapRowToCity(results));
//		}
//		return cities;
//	}
}
