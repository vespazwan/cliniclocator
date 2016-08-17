package ihs.com.cliniclocator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EMA on 9/3/2016.
 */
public class ObjectList {

    private static final String radiusforsquare = "10000";
    private static final String keyfoursquareclient = "QUHTEPCVWHSQWYHBIBE10RRZ35JVN23GWYTJ3MAM4KVEONJU";
    private static final String keyfoursquaresecret = "NP5D0GNGJSUWQTH3DF51U53ENAP3RPTFIIZ4PTDKBWNYDM0Q";
    private static final String keyfoursquareversion = "20130815%20";
    private static final String categoryId = "4bf58dd8d48988d104941735";

    // Comment either or
    //protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://apicliniclocator-ihssb.rhcloud.com/"; // PROD V1
    protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://apicliniclocatorv2-ihssb.rhcloud.com/"; // PROD V2 with rating
    //protected static final String CLINICLOCATOR_PANEL_API_ENDPOINT = "http://mywildflyrestv2-emafazillah.rhcloud.com/"; // DEV

    private String clinicsFoursquare, photoFoursquare;
    private JSONParser jsonParserFoursquare, jsonParserPhotoFoursquare;
    private JSONObject jsonObjectFoursquare, jsonClinicFoursquare, jsonObjectPhotoFoursquare;
    private JSONArray jsonClinicsFoursquare, jsonClinicPhotosFoursquare;
    private HashMap<String, String> clinicMapFoursquare;
    private ArrayList<HashMap<String, String>> clinicListFoursquare;

    private JSONParser jsonParser;
    private JSONObject jsonObject, jsonClinic, jsonPanel;
    private JSONArray jsonClinics;

    private HashMap<String, String> clinicMap;
    private HashMap<String, String> panelMap;
    private HashMap<String, String> clinicRatingMap;
    private ArrayList<HashMap<String, String>> clinicList;
    private ArrayList<HashMap<String, String>> panelList;
    private ArrayList<HashMap<String, String>> clinicRatingList;

    public ArrayList<HashMap<String, String>> ArrClinicList(double lat, double lng, String keyword){

        try{

            StringBuilder sb = new StringBuilder("https://api.foursquare.com/v2/venues/search?");
            sb.append("ll="+lat+","+lng);
            sb.append("&categoryId="+categoryId);
            if (!"".equals(keyword)) {
                sb.append("&query="+keyword);
            }
            sb.append("&radius="+radiusforsquare);
            sb.append("&client_id="+keyfoursquareclient);
            sb.append("&client_secret="+keyfoursquaresecret);
            sb.append("&v="+keyfoursquareversion);

            clinicsFoursquare = sb.toString();
            clinicListFoursquare = new ArrayList<HashMap<String,String>>();

            jsonParserFoursquare = new JSONParser();
            jsonObjectFoursquare = jsonParserFoursquare.getJSONFromUrls(clinicsFoursquare);
            jsonClinicsFoursquare = jsonObjectFoursquare.getJSONObject("response").getJSONArray("venues");

            if(jsonClinicsFoursquare.length() > 0){

                for (int i = 0; i < jsonClinicsFoursquare.length(); i++) {

                    jsonClinicFoursquare = jsonClinicsFoursquare.getJSONObject(i);
                    clinicMapFoursquare = new HashMap<String, String>();

                    //name
                    clinicMapFoursquare.put("name", jsonClinicFoursquare.getString("name"));

                    //distance
                    double jarak = 0;
                    if(jsonClinicFoursquare.getJSONObject("location").has("distance")) {
                        jarak = Double.parseDouble(jsonClinicFoursquare.getJSONObject("location").getString("distance"));
                        jarak = jarak / 1000;
                    }
                    clinicMapFoursquare.put("jarak", Double.toString(jarak));

                    //latitude
                    clinicMapFoursquare.put("lat", jsonClinicFoursquare.getJSONObject("location").getString("lat"));

                    //longitude
                    clinicMapFoursquare.put("lng", jsonClinicFoursquare.getJSONObject("location").getString("lng"));

                    //address
                    if(jsonClinicFoursquare.getJSONObject("location").has("address")) {
                        clinicMapFoursquare.put("address", jsonClinicFoursquare.getJSONObject("location").getString("address"));
                    } else {
                        clinicMapFoursquare.put("address", "Not Available");
                    }

                    //photo
                    String venueId = jsonClinicFoursquare.getString("id");
                    StringBuilder sbPhoto = new StringBuilder("https://api.foursquare.com/v2/venues/");
                    sbPhoto.append(venueId);
                    sbPhoto.append("/photos?");
                    sbPhoto.append("&client_id="+keyfoursquareclient);
                    sbPhoto.append("&client_secret="+keyfoursquaresecret);
                    sbPhoto.append("&v="+keyfoursquareversion);

                    photoFoursquare = sbPhoto.toString();

                    jsonParserPhotoFoursquare = new JSONParser();
                    jsonObjectPhotoFoursquare = jsonParserPhotoFoursquare.getJSONFromUrls(photoFoursquare);
                    jsonClinicPhotosFoursquare = jsonObjectPhotoFoursquare.getJSONObject("response").getJSONObject("photos").getJSONArray("items");
                    int countPhoto = jsonObjectPhotoFoursquare.getJSONObject("response").getJSONObject("photos").getInt("count");

                    if(countPhoto > 0){
                        String prefix = jsonClinicPhotosFoursquare.getJSONObject(0).getString("prefix");
                        String suffix = jsonClinicPhotosFoursquare.getJSONObject(0).getString("suffix");
                        String photofile = prefix + "96x96" + suffix;

                        clinicMapFoursquare.put("photo", photofile);
                    }else{
                        clinicMapFoursquare.put("photo", "Not Available");
                    }

                    //response
                    clinicMapFoursquare.put("response", "Success");

                    //mylat
                    clinicMapFoursquare.put("mylat", Double.toString(lat));

                    //mylng
                    clinicMapFoursquare.put("mylng", Double.toString(lng));

                    //put to list
                    clinicListFoursquare.add(clinicMapFoursquare);

                }

                return clinicListFoursquare;

            }else{
                return null;
            }

        }catch (Exception e) {
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> ArrClinicListNew(double lat, double lng, String keyword){

        try {

            StringBuilder sb = null;
            if(keyword != null && !"".equalsIgnoreCase(keyword)) {
                sb = new StringBuilder("http://mywildflyrestv2-emafazillah.rhcloud.com/clinicsloc/search/");
                sb.append(keyword);
                sb.append("/");
                sb.append(lat);
                sb.append("/");
                sb.append(lng);
            } else {
                //sb = new StringBuilder("http://mywildflyrestv2-emafazillah.rhcloud.com/clinicsloc/");
                sb = new StringBuilder("http://mywildflyrestv2-emafazillah.rhcloud.com/clinicsloc/load/");
                sb.append(lat);
                sb.append("/");
                sb.append(lng);
            }
            clinicList = new ArrayList<HashMap<String,String>>();
            jsonParser = new JSONParser();
            jsonObject = jsonParser.getJSONFromUrlsNew(sb.toString());
            jsonClinics = jsonObject.getJSONArray("cliniclocator");

            if(jsonClinics.length() > 0){

                for (int i = 0; i < jsonClinics.length(); i++) {

                    jsonClinic = jsonClinics.getJSONObject(i);
                    clinicMap = new HashMap<String, String>();

                    //name
                    //clinicMap.put("name", jsonClinic.getString("providerName"));
                    clinicMap.put("name", jsonClinic.getString("clinicName"));

                    //distance
                    double jarak = 0;
                    double latClinic = jsonClinic.getDouble("latitude");
                    double lngClinic = jsonClinic.getDouble("longitude");
                    jarak = distance(lat, lng, latClinic, lngClinic, "K");
                    DecimalFormat df = new DecimalFormat("###.##");
                    clinicMap.put("jarak", df.format(jarak));

                    //latitude
                    clinicMap.put("lat", Double.toString(jsonClinic.getDouble("latitude")));

                    //longitude
                    clinicMap.put("lng", Double.toString(jsonClinic.getDouble("longitude")));

                    //address
                    clinicMap.put("address", jsonClinic.getString("address"));

                    //photo
                    clinicMap.put("photo", jsonClinic.getString("tel"));

                    //response
                    clinicMap.put("response", jsonClinic.getString("panel"));

                    //mylat
                    clinicMap.put("mylat", Double.toString(lat));

                    //mylng
                    clinicMap.put("mylng", Double.toString(lng));

                    //jenis
                    clinicMap.put("jenis", jsonClinic.getString("clinicType"));

                    //put to list
                    clinicList.add(clinicMap);
                }

                return clinicList;

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> ArrClinicListLoad(double lat, double lng, String keyword){

        try {

            StringBuilder sb = new StringBuilder(CLINICLOCATOR_PANEL_API_ENDPOINT + "clinicspanels/loadsearch/");
            sb.append(keyword);
            sb.append("/");
            sb.append(lat);
            sb.append("/");
            sb.append(lng);

            clinicList = new ArrayList<HashMap<String, String>>();
            jsonParser = new JSONParser();
            jsonObject = jsonParser.getJSONFromUrlsNew(sb.toString());
            jsonClinics = jsonObject.getJSONArray("cliniclocator");

            if(jsonClinics.length() > 0){

                for (int i = 0; i < jsonClinics.length(); i++) {

                    jsonClinic = jsonClinics.getJSONObject(i);
                    clinicMap = new HashMap<String, String>();

                    //name
                    clinicMap.put("name", jsonClinic.getString("clinicName"));

                    //distance
                    double jarak = jsonClinic.getDouble("jarak");
                    DecimalFormat df = new DecimalFormat("###.##");
                    clinicMap.put("jarak", df.format(jarak));

                    //latitude
                    clinicMap.put("lat", Double.toString(jsonClinic.getDouble("latitude")));

                    //longitude
                    clinicMap.put("lng", Double.toString(jsonClinic.getDouble("longitude")));

                    //address
                    String address = jsonClinic.getString("address");
                    clinicMap.put("address", makeProper(address));

                    //telephone
                    clinicMap.put("photo", jsonClinic.getString("tel"));

                    //mylat
                    clinicMap.put("mylat", Double.toString(lat));

                    //mylng
                    clinicMap.put("mylng", Double.toString(lng));

                    //jenis
                    clinicMap.put("jenis", jsonClinic.getString("clinicType"));

                    //panel clinicMap.put("response", jsonClinic.getString("panel"));
                    clinicMap.put("response", Long.toString(jsonClinic.getLong("id")));

                    //put to list
                    clinicList.add(clinicMap);
                }

                return clinicList;

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> ArrClinicListLoadOri(double lat, double lng, String keyword){

        try {

            StringBuilder sb = new StringBuilder(CLINICLOCATOR_PANEL_API_ENDPOINT + "clinicspanels/loadsearch/");
            sb.append(keyword);
            sb.append("/");
            sb.append(lat);
            sb.append("/");
            sb.append(lng);

            ArrayList<HashMap<String, String>> arrListClinic = new ArrayList<HashMap<String, String>>();
            JSONParser jsonParserClinic = new JSONParser();
            JSONObject jsonObjectClinic = jsonParserClinic.getJSONFromUrlsNew(sb.toString());
            JSONArray jsonArrayClinic = jsonObjectClinic.getJSONArray("cliniclocator");

            if(jsonArrayClinic.length() > 0){

                for (int i = 0; i < jsonArrayClinic.length(); i++) {

                    JSONArray jaClinic = jsonArrayClinic.getJSONArray(i);

                    HashMap<String, String> hashMapClinic = new HashMap<String, String>();

                    //panel
                    hashMapClinic.put("response", jaClinic.get(0).toString());

                    //name
                    hashMapClinic.put("name", jaClinic.get(1).toString());

                    //jenis
                    hashMapClinic.put("jenis", jaClinic.get(2).toString());

                    //address
                    hashMapClinic.put("address", jaClinic.get(3).toString());

                    //latitude
                    hashMapClinic.put("lat", jaClinic.get(4).toString());

                    //longitude
                    hashMapClinic.put("lng", jaClinic.get(5).toString());

                    //telephone
                    hashMapClinic.put("photo", jaClinic.get(6).toString());

                    //distance
                    double jarak = Double.parseDouble(jaClinic.get(7).toString());
                    DecimalFormat df = new DecimalFormat("###.##");
                    hashMapClinic.put("jarak", df.format(jarak));

                    //mylat
                    hashMapClinic.put("mylat", Double.toString(lat));

                    //mylng
                    hashMapClinic.put("mylng", Double.toString(lng));

                    //put to list
                    arrListClinic.add(hashMapClinic);

                }

                return arrListClinic;

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String StrPanelListLoadOri(long clinicId){

        try {

            StringBuilder sbPanel = new StringBuilder("http://apicliniclocator-ihssb.rhcloud.com/clinicspanels/panel/");
            sbPanel.append(clinicId);

            JSONParser jsonParserPanel = new JSONParser();
            JSONObject jsonObjectPanel = jsonParserPanel.getJSONFromUrlsNew(sbPanel.toString());

            return jsonObjectPanel.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String StrPanelListLoadEma(long clinicId){

        try {

            StringBuilder sbPanelList = new StringBuilder("");
            StringBuilder sbPanel = new StringBuilder("http://apicliniclocator-ihssb.rhcloud.com/clinicspanels/panel/");
            sbPanel.append(clinicId);

            JSONParser jsonParserPanel = new JSONParser();
            JSONObject jsonObjectPanel = jsonParserPanel.getJSONFromUrlsNew(sbPanel.toString());
            JSONArray jsonArrayPanel = jsonObjectPanel.getJSONArray("cliniclocator");

            if(jsonArrayPanel.length() > 0){

                for (int i = 0; i < jsonArrayPanel.length(); i++) {

                    JSONObject joPanel = jsonArrayPanel.getJSONObject(i);
                    sbPanelList.append(joPanel.getString("panelName"));
                    if (i < (jsonArrayPanel.length() - 1)) sbPanelList.append(", ");

                }
            }

            return sbPanelList.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> StrPanelListLoad(long clinicId){

        try {

            StringBuilder sbPanel = new StringBuilder(CLINICLOCATOR_PANEL_API_ENDPOINT + "clinicspanels/panel/");
            sbPanel.append(clinicId);

            panelList = new ArrayList<HashMap<String, String>>();
            JSONParser jsonParserPanel = new JSONParser();
            JSONObject jsonObjectPanel = jsonParserPanel.getJSONFromUrlsNew(sbPanel.toString());
            JSONArray jsonArrayPanel = jsonObjectPanel.getJSONArray("cliniclocator");

            if(jsonArrayPanel.length() > 0) {
                for (int i = 0; i < jsonArrayPanel.length(); i++) {
                    jsonPanel = jsonArrayPanel.getJSONObject(i);
                    panelMap = new HashMap<String, String>();

                    //panel name
                    panelMap.put("panelName", jsonPanel.getString("panelName"));

                    panelList.add(panelMap);
                }
                return panelList;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public ArrayList<HashMap<String, String>> ArrClinicRatingListLoad(long clinicId){

        try {

            StringBuilder sb = new StringBuilder(CLINICLOCATOR_PANEL_API_ENDPOINT + "clinicsrating/clinic/");
            sb.append(clinicId);

            clinicRatingList = new ArrayList<HashMap<String, String>>();
            jsonParser = new JSONParser();
            jsonObject = jsonParser.getJSONFromUrlsNew(sb.toString());
            jsonClinics = jsonObject.getJSONArray("cliniclocator");

            if(jsonClinics.length() > 0){

                for (int i = 0; i < jsonClinics.length(); i++) {

                    jsonClinic = jsonClinics.getJSONObject(i);
                    clinicRatingMap = new HashMap<String, String>();

                    //id
                    clinicRatingMap.put("id", Double.toString(jsonClinic.getDouble("id")));

                    //userid
                    clinicRatingMap.put("userId", Integer.toString(jsonClinic.getInt("userId")));

                    //rate
                    clinicRatingMap.put("rate", Integer.toString(jsonClinic.getInt("rate")));

                    //title
                    clinicRatingMap.put("title", jsonClinic.getString("title"));

                    //description
                    clinicRatingMap.put("description", jsonClinic.getString("description"));

                    //signinUsername
                    clinicRatingMap.put("signinUsername", jsonClinic.getString("signinUsername"));

                    //put to list
                    clinicRatingList.add(clinicRatingMap);
                }

                return clinicRatingList;

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);

    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }

    public String makeProper(String theString) throws java.io.IOException{
        java.io.StringReader in = new java.io.StringReader(theString.toLowerCase());
        boolean precededBySpace = true;
        StringBuffer properCase = new StringBuffer();
        while(true) {
            int i = in.read();
            if (i == -1)  break;
            char c = (char)i;
            if (c == ' ' || c == '"' || c == '(' || c == '.' || c == '/' || c == '\\' || c == ',') {
                properCase.append(c);
                precededBySpace = true;
            } else {
                if (precededBySpace) {
                    properCase.append(Character.toUpperCase(c));
                } else {
                    properCase.append(c);
                }
                precededBySpace = false;
            }
        }

        return properCase.toString();

    }

}
