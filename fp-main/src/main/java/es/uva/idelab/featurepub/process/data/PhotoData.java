package es.uva.idelab.featurepub.process.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class PhotoData /* implements DataDAO */{

	DataSource dataSource;
	String typeName;

	String foreignId;
	String photoId;
	String preferred;

	SqlRowSet photosIds;

	public String getForeignId() {
		return foreignId;
	}

	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getPreferred() {
		return preferred;
	}

	public void setPreferred(String preferred) {
		this.preferred = preferred;
	}

	public PhotoData(DataSource dataSource, String typeName) {
		this.dataSource = dataSource;
		this.typeName = typeName;

		SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
		String sql = "SELECT * FROM " + typeName;
		this.photosIds = simpleJdbcTemplate.getJdbcOperations().queryForRowSet(sql);
	}

	public String getPreferredPhotoId(String featureId) {
		/* public Map<String, Object> getDataMap(SimpleFeature feature) { */

		Map<String, String> photos = getPhotosIds(featureId);

		Set<String> keySet = photos.keySet();
		Iterator<String> iterator = keySet.iterator();

		if (photos.containsValue("1")) {
			for (Map.Entry<String, String> entry : photos.entrySet()) {
				if (entry.getValue().equalsIgnoreCase("1"))
					return entry.getKey();
			}
		}

		else if (iterator.hasNext()) {
			return iterator.next();
		}

		return null;

	}

	public Map<String, String> getPhotosIds(String featureId) {

		photosIds.first();

		Map<String, String> photos = new HashMap<String, String>();

		if (photosIds.getRow() != 0) {
			while (photosIds.next()) {
				if (photosIds.getString(foreignId).equalsIgnoreCase(featureId)) {
					photos.put(photosIds.getString(photoId), photosIds.getString(preferred));
				}
			}
		}
		return photos;
	}
}
