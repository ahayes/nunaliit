package ca.carleton.gcrc.couch.onUpload.inReach;

import java.io.File;
import java.util.List;
import java.net.URL;

import org.json.JSONObject;

import ca.carleton.gcrc.couch.client.CouchDesignDocument;
import ca.carleton.gcrc.couch.client.impl.CouchDbImpl;
import ca.carleton.gcrc.couch.onUpload.MockCouchClient;
import ca.carleton.gcrc.couch.onUpload.MockCouchDesignDocument;
import ca.carleton.gcrc.couch.onUpload.MockFileConversionContext2;
import ca.carleton.gcrc.couch.onUpload.TestSupport;
import ca.carleton.gcrc.couch.onUpload.conversion.DocumentDescriptor;
import ca.carleton.gcrc.couch.onUpload.conversion.GeometryDescriptor;
import ca.carleton.gcrc.geom.Geometry;
import ca.carleton.gcrc.geom.Point;
import ca.carleton.gcrc.utils.TextFileUtils;
import junit.framework.TestCase;

public class InReachProcessorTest extends TestCase {

	private CouchDesignDocument testCouchDesignDocument;

	public void testPerformSubmission() throws Exception {
		try {
			testCouchDesignDocument = new MockCouchDesignDocument(
					new CouchDbImpl(new MockCouchClient(), new URL("http://test/")), new URL("http://test/"));
		} catch (Exception e) {
			fail("Could not init testing Couch Design Document:" + e);
		}
		File settingsFile = TestSupport.findResourceFile("inreach_forms.xml");
		File docFile = TestSupport.findResourceFile("inreach_doc.json");

		InReachSettingsFromXmlFile settings = new InReachSettingsFromXmlFile(settingsFile);
		settings.load();
		InReachConfiguration.setInReachSettings(settings);

		InReachProcessorImpl processor = new InReachProcessorImpl(testCouchDesignDocument);

		JSONObject jsonDoc = TextFileUtils.readJsonObjectFile(docFile);

		MockFileConversionContext2 conversionContext = new MockFileConversionContext2(jsonDoc);

		processor.performSubmission(conversionContext);

		JSONObject savedDocJson = conversionContext.getSavedDocument();
		if (null == savedDocJson) {
			fail("Document not saved");
		}
		MockFileConversionContext2 savedContext = new MockFileConversionContext2(savedDocJson);
		DocumentDescriptor savedDoc = savedContext.getDocument();

		// Test geometry
		GeometryDescriptor savedGeomDesc = savedDoc.getGeometryDescription();
		Geometry savedGeom = savedGeomDesc.getGeometry();

		if (savedGeom instanceof Point) {
			Point point = (Point) savedGeom;
			if (point.getX() < -75.65
					|| point.getX() > -75.55) {
				fail("Point Longitude should be -75.6: " + point.getX());
			}
			if (point.getY() < 45.25
					|| point.getY() > 45.35) {
				fail("Point Latitude should be 45.3: " + point.getY());
			}
		} else {
			fail("Geometry should be a Point");
		}

		// Test schema name
		String schemaName = savedDoc.getSchemaName();
		if (false == "inReach_Conditions".equals(schemaName)) {
			fail("Unexpected schema name: " + schemaName);
		}

		// Check data
		JSONObject data = savedDocJson.getJSONObject("inReach_Conditions");
		String condition = data.optString("Condition", null);
		if (false == "high water".equals(condition)) {
			fail("Unexpected data");
		}
	}

	public void testPerformGarminExploreSubmission() throws Exception {
		try {
			testCouchDesignDocument = new MockCouchDesignDocument(
					new CouchDbImpl(new MockCouchClient(), new URL("http://test/")), new URL("http://test/"));
		} catch (Exception e) {
			fail("Could not init testing Couch Design Document:" + e);
		}
		File settingsFile = TestSupport.findResourceFile("inreach_forms.xml");
		File docFile = TestSupport.findResourceFile("inreach_garminexplore_doc.json");

		InReachSettingsFromXmlFile settings = new InReachSettingsFromXmlFile(settingsFile);
		settings.load();
		InReachConfiguration.setInReachSettings(settings);

		InReachProcessorImpl processor = new InReachProcessorImpl(testCouchDesignDocument);
		JSONObject jsonDoc = TextFileUtils.readJsonObjectFile(docFile);
		MockFileConversionContext2 conversionContext = new MockFileConversionContext2(jsonDoc);
		processor.performSubmission(conversionContext);

		JSONObject savedDocJson = conversionContext.getSavedDocument();
		if (null == savedDocJson) {
			fail("Document not saved");
		}

		List<JSONObject> resDocs = conversionContext.getCreatedDocuments();
		JSONObject singleCreatedDocument = null;
		if (resDocs.size() == 1) {
			singleCreatedDocument = resDocs.get(0);
		} else {
			fail("Exactly one new document should be created with inreach_garminexplore_doc.json");
		}
		MockFileConversionContext2 savedContext = new MockFileConversionContext2(singleCreatedDocument);
		DocumentDescriptor savedDoc = savedContext.getDocument();
		GeometryDescriptor savedGeomDesc = savedDoc.getGeometryDescription();
		Geometry savedGeom = savedGeomDesc.getGeometry();

		if (savedGeom instanceof Point) {
			Point point = (Point) savedGeom;
			if (point.getX() < -75.75
					|| point.getX() > -75.73) {
				fail("Point Longitude should be -75.74515342712403: " + point.getX());
			}
			if (point.getY() < 45.38
					|| point.getY() > 45.4) {
				fail("Point Latitude should be 45.3940486907959: " + point.getY());
			}
		} else {
			fail("Geometry should be a Point");
		}

		String schemaName = savedDoc.getSchemaName();
		if (false == "inReach_Wildlife".equals(schemaName)) {
			fail("Unexpected schema name: " + schemaName);
		}

		JSONObject data = singleCreatedDocument.getJSONObject("inReach_Wildlife");
		String condition = data.optString("What", null);
		if (false == "Caribou".equals(condition)) {
			fail("Unexpected data");
		}
	}

	public void testPerformGarminExploreMultipleEventSubmission() throws Exception {
		try {
			testCouchDesignDocument = new MockCouchDesignDocument(
					new CouchDbImpl(new MockCouchClient(), new URL("http://test/")), new URL("http://test/"));
		} catch (Exception e) {
			fail("Could not init testing Couch Design Document:" + e);
		}
		File settingsFile = TestSupport.findResourceFile("inreach_forms.xml");
		File docFile = TestSupport.findResourceFile("inreach_garminexplore_multi_doc.json");

		InReachSettingsFromXmlFile settings = new InReachSettingsFromXmlFile(settingsFile);
		settings.load();
		InReachConfiguration.setInReachSettings(settings);

		InReachProcessorImpl processor = new InReachProcessorImpl(testCouchDesignDocument);
		JSONObject jsonDoc = TextFileUtils.readJsonObjectFile(docFile);
		MockFileConversionContext2 conversionContext = new MockFileConversionContext2(jsonDoc);
		processor.performSubmission(conversionContext);

		JSONObject savedDocJson = conversionContext.getSavedDocument();
		if (null == savedDocJson) {
			fail("Document not saved");
		}

		List<JSONObject> resDocs = conversionContext.getCreatedDocuments();
		if (resDocs.size() != 9) {
			fail("Exactly nine new documents should be created with inreach_garminexplore_multi_doc.json (last one fails to create document, phone number messages are removed)");
		}

		for (int i = 0; i < resDocs.size(); i++) {
			JSONObject createdDocument = resDocs.get(i);
			MockFileConversionContext2 savedContext = new MockFileConversionContext2(createdDocument);
			DocumentDescriptor savedDoc = savedContext.getDocument();
			String schemaName = savedDoc.getSchemaName();
			JSONObject jsonItem = createdDocument.getJSONObject("inReach");
			JSONObject jsonTimestamp = jsonItem.optJSONObject("nunaliit_gps_datetime");
			String messageType = jsonItem.getString("MessageType");
			Integer emergencyState = jsonItem.getInt("EmergencyState");

			if (i == 0) {
				if (false == "inReach_Wildlife".equals(schemaName)) {
					fail("Unexpected schema name: " + schemaName);
				}
				if (!messageType.equals("FreeTextMessage")) {
					fail("Wrong MessageType from created document: " + messageType);
				}
				if (emergencyState != -1) {
					fail("EmergencyState expected to be -1");
				}
			} else if (i == 1) {
				if (false == "inReach_Place".equals(schemaName)) {
					fail("Unexpected schema name: " + schemaName);
				}
				if (!messageType.equals("FreeTextMessage")) {
					fail("Wrong MessageType from created document: " + messageType);
				}
				if (emergencyState != -1) {
					fail("EmergencyState expected to be -1");
				}
			} else if (i == 2) {
				if (false == "inReach_Issues".equals(schemaName)) {
					fail("Unexpected schema name: " + schemaName);
				}
				if (!messageType.equals("FreeTextMessage")) {
					fail("Wrong MessageType from created document: " + messageType);
				}
				if (emergencyState != -1) {
					fail("EmergencyState expected to be -1");
				}
			} else if (i == 3) {
				if (false == "inReach".equals(schemaName)) {
					fail("Unexpected schema name: " + schemaName);
				}
				if (!messageType.equals("MailCheck")) {
					fail("Wrong MessageType from created document: " + messageType);
				}
				if (emergencyState != -1) {
					fail("EmergencyState expected to be -1");
				}
			} else if (i == 4) {
				if (!messageType.equals("NunaliitUnhandledGarminExploreMessageCode-5")) {
					fail("Expected unhandled message code string for unimplemented/unknown message codes");
				}
				if (emergencyState != -1) {
					fail("EmergencyState expected to be -1");
				}
			} else if (i == 5) {
				if (!messageType.equals("DeclareSOS")) {
					fail("Wrong MessageType from created document: " + messageType);
				}
				if (emergencyState == -1) {
					fail("EmergencyState should be set");
				}
			} else if (i == 6) {
				if (!messageType.equals("ConfirmSOS")) {
					fail("Wrong MessageType from created document: " + messageType);
				}
				if (emergencyState == -1) {
					fail("EmergencyState should be set");
				}
			} else if (i == 7) {
				if (!messageType.equals("CancelSOS")) {
					fail("Wrong MessageType from created document: " + messageType);
				}
				if (emergencyState == -1) {
					fail("EmergencyState should be set");
				}
			} else if (i == 8) {
				if (!messageType.equals("NunaliitUnhandledGarminExploreMessageCode-100")) {
					fail("Wrong MessageType from created document: " + messageType);
				}
				try {
					createdDocument.getJSONObject("nunaliit_geom");
					fail("No geom should be created when latitude and longitude are 0");
				} catch (Exception e) {
				}

			}

			if (null == jsonTimestamp) {
				fail("Can not find nunaliit_gps_datetime!");
			} else {
				String dateType = jsonTimestamp.optString("nunaliit_type", null);
				if (false == "date".equals(dateType)) {
					fail("Unexpected time structure");
				}
			}
		}
	}

	public void testMissingField() throws Exception {
		try {
			testCouchDesignDocument = new MockCouchDesignDocument(
					new CouchDbImpl(new MockCouchClient(), new URL("http://test/")), new URL("http://test/"));
		} catch (Exception e) {
			fail("Could not init testing Couch Design Document:" + e);
		}
		File settingsFile = TestSupport.findResourceFile("inreach_forms.xml");
		File docFile = TestSupport.findResourceFile("inreach_doc_missing.json");

		InReachSettingsFromXmlFile settings = new InReachSettingsFromXmlFile(settingsFile);
		settings.load();
		InReachConfiguration.setInReachSettings(settings);

		InReachProcessorImpl processor = new InReachProcessorImpl(testCouchDesignDocument);

		JSONObject jsonDoc = TextFileUtils.readJsonObjectFile(docFile);

		MockFileConversionContext2 conversionContext = new MockFileConversionContext2(jsonDoc);

		processor.performSubmission(conversionContext);

		JSONObject savedDocJson = conversionContext.getSavedDocument();
		if (null == savedDocJson) {
			fail("Document not saved");
		}
		MockFileConversionContext2 savedContext = new MockFileConversionContext2(savedDocJson);
		DocumentDescriptor savedDoc = savedContext.getDocument();

		// Test schema name
		String schemaName = savedDoc.getSchemaName();
		if (false == "inReach_Conditions".equals(schemaName)) {
			fail("Unexpected schema name: " + schemaName);
		}

		// Check data
		JSONObject data = savedDocJson.getJSONObject("inReach_Conditions");
		{
			String condition = data.optString("Condition", null);
			if (false == "other (put in notes)".equals(condition)) {
				fail("Unexpected data for 'Condition'");
			}
		}
		{
			String probs = data.optString("Causing problems?", null);
			if (null != probs) {
				fail("Unexpected data for 'Causing problems?'");
			}
		}
	}

	public void testGpsTimestamp() throws Exception {
		try {
			testCouchDesignDocument = new MockCouchDesignDocument(
					new CouchDbImpl(new MockCouchClient(), new URL("http://test/")), new URL("http://test/"));
		} catch (Exception e) {
			fail("Could not init testing Couch Design Document:" + e);
		}
		File settingsFile = TestSupport.findResourceFile("inreach_forms.xml");
		File docFile = TestSupport.findResourceFile("inreach_doc_missing.json");

		InReachSettingsFromXmlFile settings = new InReachSettingsFromXmlFile(settingsFile);
		settings.load();
		InReachConfiguration.setInReachSettings(settings);

		InReachProcessorImpl processor = new InReachProcessorImpl(testCouchDesignDocument);

		JSONObject jsonDoc = TextFileUtils.readJsonObjectFile(docFile);

		MockFileConversionContext2 conversionContext = new MockFileConversionContext2(jsonDoc);

		processor.performSubmission(conversionContext);

		JSONObject savedDocJson = conversionContext.getSavedDocument();
		if (null == savedDocJson) {
			fail("Document not saved");
		}

		// Look for time structure
		JSONObject jsonItem = savedDocJson.getJSONObject("inReach");
		JSONObject jsonTimestamp = jsonItem.optJSONObject("nunaliit_gps_datetime");
		if (null == jsonTimestamp) {
			fail("Can not find nunaliit_gps_datetime!");
		} else {
			String dateType = jsonTimestamp.optString("nunaliit_type", null);
			if (false == "date".equals(dateType)) {
				fail("Unexpected time structure");
			}
		}
	}

	public void testGarminExploreAutoCreateInReachSignout() throws Exception {
		try {
			testCouchDesignDocument = new MockCouchDesignDocument(
					new CouchDbImpl(new MockCouchClient(), new URL("http://test/")), new URL("http://test/"));
		} catch (Exception e) {
			fail("Could not init testing Couch Design Document:" + e);
		}
		File settingsFile = TestSupport.findResourceFile("inreach_forms.xml");
		File docFile = TestSupport.findResourceFile("inreach_garminexplore_auto_create_signout_doc.json");

		InReachSettingsFromXmlFile settings = new InReachSettingsFromXmlFile(settingsFile);
		settings.load();
		InReachConfiguration.setInReachSettings(settings);

		InReachProcessorImpl processor = new InReachProcessorImpl(testCouchDesignDocument);
		JSONObject jsonDoc = TextFileUtils.readJsonObjectFile(docFile);
		MockFileConversionContext2 conversionContext = new MockFileConversionContext2(jsonDoc);
		processor.performSubmission(conversionContext);

		JSONObject savedDocJson = conversionContext.getSavedDocument();
		if (null == savedDocJson) {
			fail("Document not saved");
		}

		List<JSONObject> resDocs = conversionContext.getCreatedDocuments();
		JSONObject singleCreatedDocument = null;
		if (resDocs.size() == 1) {
			singleCreatedDocument = resDocs.get(0);
		} else {
			fail("Exactly one new document should be created with inreach_garminexplore_auto_create_signout_doc.json");
		}
		MockFileConversionContext2 savedContext = new MockFileConversionContext2(singleCreatedDocument);
		DocumentDescriptor savedDoc = savedContext.getDocument();

		String schemaName = savedDoc.getSchemaName();
		if (false == "inReach_Signout".equals(schemaName)) {
			fail("Unexpected schema name: " + schemaName);
		}

		JSONObject data = singleCreatedDocument.getJSONObject("inReach_Signout");
		String name = data.optString("Name", null);
		if (false == "John Smith".equals(name)) {
			fail("Unexpected data");
		}
		String device_ref = data.optString("device_ref", null);
		if (false == "123456".equals(device_ref)) {
			fail("Unexpected data");
		}
	}

	public void testGeoProAutoCreateInReachSignout() throws Exception {
		try {
			testCouchDesignDocument = new MockCouchDesignDocument(
					new CouchDbImpl(new MockCouchClient(), new URL("http://test/")), new URL("http://test/"));
		} catch (Exception e) {
			fail("Could not init testing Couch Design Document:" + e);
		}
		File settingsFile = TestSupport.findResourceFile("inreach_forms.xml");
		File docFile = TestSupport.findResourceFile("inreach_geopro_auto_create_signout_doc.json");

		InReachSettingsFromXmlFile settings = new InReachSettingsFromXmlFile(settingsFile);
		settings.load();
		InReachConfiguration.setInReachSettings(settings);

		InReachProcessorImpl processor = new InReachProcessorImpl(testCouchDesignDocument);
		JSONObject jsonDoc = TextFileUtils.readJsonObjectFile(docFile);
		MockFileConversionContext2 conversionContext = new MockFileConversionContext2(jsonDoc);
		processor.performSubmission(conversionContext);

		JSONObject savedDocJson = conversionContext.getSavedDocument();
		if (null == savedDocJson) {
			fail("Document not saved");
		}

		MockFileConversionContext2 savedContext = new MockFileConversionContext2(savedDocJson);
		DocumentDescriptor savedDoc = savedContext.getDocument();

		String schemaName = savedDoc.getSchemaName();
		if (false == "inReach_Signout".equals(schemaName)) {
			fail("Unexpected schema name: " + schemaName);
		}

		JSONObject data = savedDocJson.getJSONObject("inReach_Signout");
		String name = data.optString("Name", null);
		if (false == "Jane Doe".equals(name)) {
			fail("Unexpected data");
		}
		String device_ref = data.optString("device_ref", null);
		if (false == "300434062312480".equals(device_ref)) {
			fail("Unexpected data");
		}
	}
}
