package net.filebot.web;

import static java.util.Arrays.*;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class SeriesInfo implements Serializable {

	// request parameters
	protected String database;
	protected String order;
	protected String language;

	// series parameters
	protected Integer id;
	protected String name;
	protected String[] aliasNames;
	protected String[] actors;
	protected String certification;
	protected SimpleDate startDate;
	protected String[] genres;
	protected String network;
	protected Double rating;
	protected Integer ratingCount;
	protected Integer runtime;
	protected String status;

	protected SeriesInfo() {

	}

	public SeriesInfo(SeriesInfo other) {
		this.database = other.database;
		this.order = other.order;
		this.language = other.language;
		this.id = other.id;
		this.name = other.name;
		this.aliasNames = other.aliasNames == null ? null : other.aliasNames.clone();
		this.actors = other.actors == null ? null : other.actors.clone();
		this.certification = other.certification;
		this.startDate = other.startDate == null ? null : other.startDate.clone();
		this.genres = other.genres == null ? null : other.genres.clone();
		this.network = other.network;
		this.rating = other.rating;
		this.ratingCount = other.ratingCount;
		this.runtime = other.runtime;
		this.status = other.status;
	}

	public SeriesInfo(String database, SortOrder order, Locale language, Integer id) {
		this.database = database;
		this.order = order.name();
		this.language = language.getLanguage();
		this.id = id;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getDatabase() {
		return database;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOrder() {
		return order;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAliasNames() {
		return aliasNames == null ? asList() : asList(aliasNames.clone());
	}

	public void setAliasNames(List<String> aliasNames) {
		this.aliasNames = aliasNames.toArray(new String[aliasNames.size()]);
	}

	public List<String> getActors() {
		return actors == null ? asList() : asList(actors.clone());
	}

	public void setActors(List<String> actors) {
		this.actors = actors.toArray(new String[actors.size()]);
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public SimpleDate getStartDate() {
		return startDate;
	}

	public void setStartDate(SimpleDate startDate) {
		this.startDate = startDate;
	}

	public List<String> getGenres() {
		return genres == null ? asList() : asList(genres.clone());
	}

	public void setGenres(List<String> genres) {
		this.genres = genres.toArray(new String[genres.size()]);
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Integer getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Integer getRuntime() {
		return runtime;
	}

	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof SeriesInfo) {
			SeriesInfo other = (SeriesInfo) object;
			return id.equals(other.id) && database.equals(other.database);
		}
		return false;
	}

	@Override
	public SeriesInfo clone() {
		return new SeriesInfo(this);
	}

	@Override
	public String toString() {
		return database + "::" + id;
	}

}
