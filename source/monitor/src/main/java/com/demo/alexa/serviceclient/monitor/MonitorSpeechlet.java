package com.demo.alexa.serviceclient.monitor;

import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class MonitorSpeechlet implements Speechlet {
	
	// Logger
	static final Logger log = Logger.getLogger(MonitorSpeechlet.class);

	// Configuration properties - to be initialized in a static block below
    private static final Properties configProperties;
    
    // Intents
    private static final String RECORD_INTENT = "RecordIsIntent";
    private static final String MRN_INTENT = "MRNIsIntent";
    private static final String PATIENT_CORRECT_INTENT = "PatientCorrectIsIntent";
    private static final String PATIENT_WRONG_INTENT = "PatientWrongIsIntent";
    private static final String TEMPERATURE_INTENT = "TemperatureIsIntent";
    private static final String PULSE_INTENT = "PulseIsIntent";
    private static final String DIASP_INTENT = "DiaspIsIntent";
    private static final String SYSP_INTENT = "SyspIsIntent";
    private static final String BYE_INTENT = "ByeIsIntent";
    private static final String ABNORMAL_VITALS_INTENT = "AbnormalVitalsIsIntent";
    private static final String READ_VITALS_INTENT = "ReadVitalIsIntent";
    
    // Slots
    private static final String MRN_SLOT = "mrn";
    private static final String TEMPERATURE_SLOT = "temperature";
    private static final String PULSE_SLOT = "pulse";
    private static final String SYSP_SLOT = "sysp";
    private static final String DIASP_SLOT = "diasp";
    
    // Vital names
    private static final String TEMPERATURE = "temperature";
    private static final String PULSE = "pulse";
    private static final String SYSP = "systolic pressure";
    private static final String DIASP = "diastolic pressure";
    
    // Keys - those are the attribute names to hold variables in session
    private static final String PATIENT_KEY = "Patient_Key";
    private static final String TEMPERATURE_KEY = "Temperature_Key";
    private static final String PULSE_KEY = "Pulse_Key";
    private static final String SYSP_KEY = "Sysp_Key";
    private static final String DIASP_KEY = "Diasp_Key";
    private static final String EXISTING_TEMPERATURE_KEY = "Existing_Temperature_Key";
    private static final String EXISTING_PULSE_KEY = "Existing_Pulse_Key";
    private static final String EXISTING_SYSP_KEY = "Existing_Sysp_Key";
    private static final String EXISTING_DIASP_KEY = "Existing_Diasp_Key";
    
    // Configuration file
    private static final String CONFIG_FILE_NAME = "configuration.properties";

    // Configuration file entries below help construct the REST endpoint  
    // for the medical record management service
    private static final String SERVICE_ENDPOINT = "serviceEndpoint";
    private static final String CONTEXT_PATH = "contextPath";
    private static final String ID_QRY_PATH = "idQryPath";
    private static final String ABNORMAL_QRY_PATH = "abnormalQryPath";
    private static final String SAVE_VITALS_PATH = "saveVitalPath";

    // Other constants
    private static final String REQ_METHOD = "GET";

    static {
    	InputStream is = null;
  	   	configProperties = new Properties();
  	  try{
		  is = MonitorSpeechlet.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
		  configProperties.load(is);
	  } catch (IOException e) {
		log.error("Cannot load properties: " + e.getLocalizedMessage());
	} 
	  finally{
		  try {
			is.close();
		} catch (IOException e) {
			log.error("Cannot close stream: " + e.getLocalizedMessage());
		}
	  }
    }

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId: " + request.getRequestId() + ", sessionId: " +
                session.getSessionId());
        log.info("User: " + session.getUser().getUserId() + 
        		", Access Token: " + session.getUser().getAccessToken());
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId: " + request.getRequestId() + ", sessionId: " +
                session.getSessionId());
        return getWelcomeResponse();
    } 
 
    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        
        switch (intentName) {
	        case RECORD_INTENT:
	        	return recordIntentHandler(session);
	        
	        case MRN_INTENT:
	        	return mrnIntentHandler(session, intent);
        	
	        case PATIENT_WRONG_INTENT:
	        	return wrongPatientIntentHandler(session);
	        	
	        case PATIENT_CORRECT_INTENT:
	        	return correctPatientIntentHandler();
	        	
	        case TEMPERATURE_INTENT:
	        	return vitalIntentHandler(session, intent, TEMPERATURE_SLOT, TEMPERATURE, TEMPERATURE_KEY);	
	        	
	        case PULSE_INTENT:
	        	return vitalIntentHandler(session, intent, PULSE_SLOT, PULSE, PULSE_KEY);	
	        	
	        case SYSP_INTENT:
	        	return vitalIntentHandler(session, intent, SYSP_SLOT, SYSP, SYSP_KEY);	
	        	
	        case DIASP_INTENT:
	        	return vitalIntentHandler(session, intent, DIASP_SLOT, DIASP, DIASP_KEY);	
	        	
	        case ABNORMAL_VITALS_INTENT:
	        	return abnormalVitalsIntentHandler(session);
	        	
	        case READ_VITALS_INTENT:
	        	return readVitalIntentHandler(session, intent);
	        	
	        case BYE_INTENT:
	        	return byeIntentHandler(session);	
	        	
	        default:
	        	throw new SpeechletException("Invalid Intent");
        }
    }
    
    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId: " + request.getRequestId() + ", sessionId: " +
                session.getSessionId());
    }
    
    // Handlers
    
    /**
     * Called when user wants to record vitals for a patient. It cleans up session from any 
     * previous patient information and initiates conversation with the user by asking
     * medical record number of the patient.
     * 
     * @param Session 
     * @return SpeechletResponse
     */
    protected SpeechletResponse recordIntentHandler(Session session){
    	cleanupSession(session);
    	return initiateConversation();
    }

    /**
     * Called when user wants to learn about the vitals of a patient by supplying its medical record number. 
     * Firstly, if there are any vitals to be recorded, it saves them in the medical record management service. 
     * Then, it cleans up the session and queries the medical record management service for the particular patient. 
     * If patient is found, reads back vitals of the patient to user. If patient does not exist, it lets user know 
     * that patient cannot be found and requests the medical record number of the patient again.
     * 
     * @param Session
     * @param Intent
     * @return SpeechletResponse
     */
    protected SpeechletResponse readVitalIntentHandler(Session session, Intent intent){
		// Save any vitals we have stored in session so far and cleanup
		saveVitals(session);
		cleanupSession(session);
		
		// Now continue with the actual task
    	String mrn = intent.getSlot(MRN_SLOT).getValue();
    	if(mrn != null){
    		Patient patient = getPatientByNumber(mrn);
    		
    		if(patient == null || !mrn.equals(patient.getNumber().toString())){
    			return repeatRequestForMRN();
    		}else{
    			StringBuilder sb = new StringBuilder();
    			sb.append("This patient, ").append(patient.getName()).append(", has the following vitals");
    			
    			
    			if(patient.getTemperature() != null){
    				sb.append(", temperature is " ).append(patient.getTemperature().toString());
    			}
    			
    			if(patient.getPulse() != null){
    				sb.append(", pulse is " ).append(patient.getPulse().toString());
    			}
    			
    			if(patient.getSysp() != null){
    				sb.append(", systolic pressure is " ).append(patient.getSysp().toString());
    			}
    			
    			if(patient.getDiasp() != null){
    				sb.append(", diastolic pressure is " ).append(patient.getDiasp().toString());
    			}
    			sb.append(". Is there anything else you need?");
    			
    			String speechText = sb.toString();
    			String repromptText = "Is there anything else you need?";
    			return getSpeechletResponse(speechText, repromptText, true);
    		}
    	}
    	else return repeatRequestForMRN();
    }
    
    /**
     * As part of the dialogue with user who wants to record a patient vital, this handler
     * is called when user provides the medical record number of the patient. This handler 
     * obtains the patient information from external service, medical record management system,
     * and stores the information session. Then, reads patient name back to user asking confirmation.
     * 
     * @param Session
     * @param Intent
     * @return SpeechletResponse
     */
    protected SpeechletResponse mrnIntentHandler(Session session, Intent intent){
    	String mrn = intent.getSlot(MRN_SLOT).getValue();
    	if(mrn != null){
    		Patient patient = getPatientByNumber(mrn);
    		log.info("mrn: " + mrn);
    		if(patient != null){
    			log.info("patient mrn: " + patient.getNumber());
    		}
    		
    		
    		if(patient == null){
    			return repeatRequestForMRN();
    		}else{
    			session.setAttribute(PATIENT_KEY, patient.getNumber().toString());

    	    	Integer existingTemperatureI = patient.getTemperature();
    	    	String existingTemperature = existingTemperatureI==null?"":existingTemperatureI.toString();
    	    	session.setAttribute(EXISTING_TEMPERATURE_KEY,existingTemperature);
    	    	
    	    	Integer existingPulseI = patient.getPulse();
    	    	String existingPulse = existingPulseI==null?"":existingPulseI.toString();
    	    	session.setAttribute(EXISTING_PULSE_KEY,existingPulse);
    	    	
    	    	Integer existingSyspI = patient.getSysp();
    	    	String existingSysp = existingSyspI==null?"":existingSyspI.toString();
    	    	session.setAttribute(EXISTING_SYSP_KEY,existingSysp);
    	    	
    	    	Integer existingDiaspI = patient.getDiasp();
    	    	String existingDiasp = existingDiaspI==null?"":existingDiaspI.toString();
    	    	session.setAttribute(EXISTING_DIASP_KEY,existingDiasp);

    	    	String speechText = "Please confirm patient's name is " + patient.getName();
    	        String repromptText = speechText;
    	        return getSpeechletResponse(speechText, repromptText, true);
    		}
    	}else{
    		return repeatRequestForMRN();
    	}
    }
    
    
    /**
     * Called if user confirms the patient name returned by mrnIntentHandler. In response,
     * it asks patient to enter a vital to be recorded.
     * 
     * @return SpeechletResponse
     */
    protected SpeechletResponse correctPatientIntentHandler(){
    	return confirmVitalGetNext(null);
    }
    
    /**
     * Called if user does not confirm the patient name returned by mrnIntentHandler. In response,
     * it cleans up session from the previous patient information and initiates conversation again by asking
     * medical record number of the patient.
     * 
     * @param Session
     * @return SpeechletResponse
     */
    protected SpeechletResponse wrongPatientIntentHandler(Session session){
    	cleanupSession(session);
    	return initiateConversation();
    }
    
    /**
     * Called when user wants to get a list of patients who have any vitals in abnormal range. Firstly,
     * if there are any vitals to be recorded, it saves them in the medical record management service. Then, 
     * it cleans up the session and queries the medical record management service for a list of patients who 
     * have any vitals in abnormal range. If list is not empty, reads the patients to user.
     *  
     * @param Session
     * @return SpeechletResponse
     */
	protected SpeechletResponse abnormalVitalsIntentHandler(Session session) {
		// Save any vitals we have stored in session so far and cleanup
		saveVitals(session);
		cleanupSession(session);
		String repromptText = "Is there anything else you need?";
		
		// Continue with the actual request
		StringBuilder sb = new StringBuilder();
		sb.append(configProperties.getProperty(SERVICE_ENDPOINT))
				.append(configProperties.getProperty(CONTEXT_PATH))
				.append(configProperties.getProperty(ABNORMAL_QRY_PATH));
		String json = null;
		try {
			json = processGetRequest(sb.toString());
		} catch (IOException e) {
			log.error("IOException while getting json "
					+ e.getLocalizedMessage());
			return sayBye(true, session);
		}
		List<Patient> patients = parsePatients(json);
		if (patients.size() == 1 && patients.get(0).getId() == -1L) {
			String speechText = "There are no patients with abnormal vitals. Is there anything else you need?";
			
			return getSpeechletResponse(speechText, repromptText, true);
		} else if (patients.size() == 1) {
			String speechText = "Patient " + patients.get(0).getName()
					+ " has abnormal vitals. Is there anything else you need?";
			
			return getSpeechletResponse(speechText, repromptText, true);
		} else {
			StringBuilder result = new StringBuilder("Patients ");

			int i = 0;
			for (Patient patient : patients) {
				i++;
				if (i == patients.size()) {
					result.append("and ").append(patient.getName());
				} else {
					result.append(patient.getName()).append(", ");
				}
			}
			result.append(" have abnormal vitals. Is there anything else you need?");
			String speechText = result.toString();
			
			return getSpeechletResponse(speechText, repromptText, true);
		}

	}
    

    /**
     * Called when patient ends conversation with the skill. Firstly, if there are any vitals to be recorded, it saves them 
     * in the medical record management service. Then, it cleans up the session. Finally, it returns a bye message to user.
     * 
     * @param Session
     * @return SpeechletResponse
     */
    protected SpeechletResponse byeIntentHandler(Session session){
    	saveVitals(session);
    	cleanupSession(session); 
		return sayBye(false, session);
    }
    
    /**
     * For the particular patient in session, stores a vital temporarily in session to be saved later on.
     * Because user enters one vital at a time, e.g. systolic blood pressure, diastolic blood pressure, temperate, 
     * and pulse, this handler is called once for each vital. Later on, if user ends the conversation or indicates
     * another intent, the corresponding intent handler will gather all the vitals from session and save them in
     * medical record management service at once. 
     * 
     * @param Session
     * @param Intent
     * @param String slotName
     * @param String vitalName
     * @param String vitalKey
     * @return
     */
    protected SpeechletResponse vitalIntentHandler(Session session, Intent intent, String slotName, 
    		String vitalName, String vitalKey){
    	
    	String mrn = (String)session.getAttribute(PATIENT_KEY);
    	if(mrn == null){
    		log.info("Patient key is null");
    		return initiateConversation();
    	}
    	
    	Integer vital = null;
    	String value = intent.getSlot(slotName).getValue();
    	try{
    		vital = Integer.parseInt(value);
    	}catch(NumberFormatException e){
    		log.error("Cannot parse " + vitalName + "; value = " + value);
    		return repeatVital(vitalName);
    	}
    	session.setAttribute(vitalKey, vital);
    	return confirmVitalGetNext(vitalName);
    }
    
    // Helpers
    
    /**
     * Returns user a bye message. If error parameter is true, the message also indicates that an error has occurred.
     * 
     * @param boolean error
     * @param Session 
     * @return SpeechletResponse
     */
    protected SpeechletResponse sayBye(boolean error, Session session){
    	String speechText = null;
    	if(error){
        	speechText = "An error occured. Please try again later. Bye.";
    	}else{
        	speechText = "Thanks. Bye.";
    	}
    	
        return getSpeechletResponse(speechText, null, false);
    }
        
    /**
     * Cleans up current session.
     * 
     * @param Session
     */
    protected void cleanupSession(Session session){    	
    	session.removeAttribute(PATIENT_KEY);    	
    	session.removeAttribute(TEMPERATURE_KEY);    	
    	session.removeAttribute(PULSE_KEY);    	
    	session.removeAttribute(SYSP_KEY);    	
    	session.removeAttribute(DIASP_KEY);
    	session.removeAttribute(EXISTING_TEMPERATURE_KEY);    	
    	session.removeAttribute(EXISTING_PULSE_KEY);    	
    	session.removeAttribute(EXISTING_SYSP_KEY);    	
    	session.removeAttribute(EXISTING_DIASP_KEY);
    }
    
    /**
     * Creates a response asking user to repeat the medical record number of the patient because the previously supplied 
     * medical record number did not match any patient.
     * 
     * @return SpeechletResponse
     */
    private SpeechletResponse repeatRequestForMRN(){
    	String speechText = "I cannot find the patient you requested. Please repeat medical record number of the patient.";
        String repromptText = "Please tell me the medical record number of the patient.";
        return getSpeechletResponse(speechText, repromptText, true);
    }
    
    /**
     * Creates a response asking user to provide medical record number of the patient. This is used when user sends an intent
     * requiring the skill locate a patient in medical record management service.
     * 
     * @return SpeechletResponse
     */
    private SpeechletResponse initiateConversation(){
    	String speechText = "What is medical record number of the patient?";
        String repromptText = "Please tell me the medical record number of the patient.";
        return getSpeechletResponse(speechText, repromptText, true);
    }
    
    /**
     * If recordedVital is not null, confirms that the vital has been recorded and asks user to enter 
     * a vital. Otherwise, it asks user to enter a vital.
     * 
     * @param String recordedVital
     * @return SpeechletResponse
     */
    private SpeechletResponse confirmVitalGetNext(String recordedVital){
    	String speechText = "Please enter vital.";
    	if(recordedVital != null){
    		speechText = recordedVital + " is recorded. " + speechText;
    	}
    	String repromptText = speechText;
        return getSpeechletResponse(speechText, repromptText, true);
    }
    
    /**
     * Returns a response indicating that the vital supplied by user could not be understood and therefore
     * it needs to be repeated.
     * 
     * @param String vital
     * @return SpeechletResponse
     */
    private SpeechletResponse repeatVital(String vital){
    	String speechText = "I did not understand " + vital + ", please enter again.";
    	String repromptText = speechText;
        return getSpeechletResponse(speechText, repromptText, true);
    }
    

    /**
     * Gathers all the vitals temporarily saved in session so far for the particular patient
     * and calls setVitals helper method to save them in the medical record management service. 
     * 
     * @param Session
     */
    protected void saveVitals(Session session){
    	String mrn = (String)session.getAttribute(PATIENT_KEY);
    	if(mrn == null){
    		log.info("Patient key is null");
    		return;
    	}

    	Integer newTemperature = (Integer) session.getAttribute(TEMPERATURE_KEY);
    	Integer newPulse = (Integer) session.getAttribute(PULSE_KEY);
    	Integer newSysp = (Integer) session.getAttribute(SYSP_KEY);
    	Integer newDiasp = (Integer) session.getAttribute(DIASP_KEY);
    	
    	if(newTemperature == null && newPulse == null && newSysp == null && newDiasp == null){
    		return;
    	}

    	String existingTemperature = (String)session.getAttribute(EXISTING_TEMPERATURE_KEY);
    	String existingPulse = (String)session.getAttribute(EXISTING_PULSE_KEY);
    	String existingSysp = (String)session.getAttribute(EXISTING_SYSP_KEY);
    	String existingDiasp = (String)session.getAttribute(EXISTING_DIASP_KEY);
    	
    	String response = null;
    	try {
			response = setVitals(mrn,
					(newDiasp==null?existingDiasp:newDiasp.toString()),
					(newSysp==null?existingSysp:newSysp.toString()),
					(newPulse==null?existingPulse:newPulse.toString()),
					(newTemperature==null?existingTemperature:newTemperature.toString())
					);
			log.info("debug: " + response);
		} catch (IOException e) {			
			log.error("Cannot save vital: " + e.getLocalizedMessage());
		}  
    	return;
    }
    
    /**
     * For the medical record number supplied as the input parameter, creates a 
     * REST call URL to access the medical record management service for saving the
     * patient vitals.
     * 
     * @param Integer patientNumber
     * @param String diasp
     * @param String sysp
     * @param String pulse
     * @param String temperature
     * @return String
     * @throws IOException
     */
    protected String setVitals(String mrn, String diasp, String sysp, String pulse, String temperature) 
  		  throws IOException{
  	  StringBuilder sb = new StringBuilder();
  	  sb.append(configProperties.getProperty(SERVICE_ENDPOINT)).
  	  	append(configProperties.getProperty(CONTEXT_PATH)).
  	  	append(configProperties.getProperty(SAVE_VITALS_PATH)).
  	  	append("/").append(mrn).
  	  	append("/").append(diasp).
  	  	append("/").append(sysp).
  	  	append("/").append(pulse).
  	  	append("/").append(temperature);
  	  return processGetRequest(sb.toString());
    }
    
    /**
     * Creates a REST call URL to access the medical record management service for obtaining a
     * list of patients who have vitals in the abnormal range. Uses processGetRequest helper
     * method to query the medical record management service for obtaining the list
     * and returns it as List<Patient> object.
     * 
     * @return List<Patient>
     * @throws IOException
     */
    protected List<Patient> getAbnormalVitals() throws IOException{
  	  StringBuilder sb = new StringBuilder();
  	  sb.append(configProperties.getProperty(SERVICE_ENDPOINT)).
  	  	append(configProperties.getProperty(CONTEXT_PATH)).
  	  	append(configProperties.getProperty(ABNORMAL_QRY_PATH));
  	  String json = processGetRequest(sb.toString());
  	  return parsePatients(json);
    }
    

    /**
     * For the medical record number supplied as the input parameter, creates a 
     * REST call URL to access the medical record management service for obtaining the
     * patient record. Uses processGetRequest helper method to query the medical 
     * record management service and returns the corresponding Patient object.
     * 
     * @param String mrn
     * @return Patient
     */
    protected Patient getPatientByNumber(String mrn){
  	  StringBuilder sb = new StringBuilder();
  	  sb.append(configProperties.getProperty(SERVICE_ENDPOINT)).
  	  	append(configProperties.getProperty(CONTEXT_PATH)).
  	  	append(configProperties.getProperty(ID_QRY_PATH)).
  	  	append("/").append(mrn);
  	  String json = null;
  	  try{
  		  json = processGetRequest(sb.toString());
  	  }catch(IOException e){
  		  log.error("IOException while getting json " + e.getLocalizedMessage());
  		  Patient patient = new Patient();
  		  patient.setId(-1l);
  		  return patient;
  	  }
  	  
  	  return parsePatient(json);
    }
    
    /**
     * Invokes an HTTP GET request for the URL supplied as the input parameter and 
     * returns the result as a String object.
     * 
     * @param String urlToGet
     * @return String
     * @throws IOException
     */
    protected String processGetRequest(String urlToGet) throws IOException{
  	    StringBuilder result = new StringBuilder();
  	    URL url = new URL(urlToGet);
  	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
  	    conn.setRequestMethod(REQ_METHOD);
  	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
  	    String line;
  	    while ((line = rd.readLine()) != null) {
  	      result.append(line);
  	    }
  	    rd.close();
  	    return result.toString();
  	  }
    
    /**
     * From a json formatted String object that has patient attributes, constructs and returns the 
     * corresponding Patient object.
     * 
     * @param String json
     * @return Patient
     */
    protected Patient parsePatient(String json){
  	  return (new Gson()).fromJson(json, Patient.class);
    }
    
    /**
     * From a json formatted String object that has attributes for a list of patients, constructs 
     * and returns the corresponding List<Patient> object.
     * 
     * @param String json
     * @return List<Patient>
     */
    protected List<Patient> parsePatients(String json){
  	  Type arrayType = new TypeToken<List<Patient>>() {}.getType();
  	  return (new Gson()).fromJson(json, arrayType);
    } 
    
    /**
     * Defines a greeting text to be read when user activates the skill without a specific intent.
     * Calls getSpeechletResponse for generating the SpeechletResponse.
     * 
     * @return SpeechletResponse
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechText =
                "Welcome to the patient monitor. You can record patient vitals, ask about current vitals or " +
                		" query patients with abnormal vitals";
        String repromptText = speechText;
        return getSpeechletResponse(speechText, repromptText, true);
    }
    
    /**
     * If isAskResponse is true, generates an 'ask response' via SpeechletResponse.newAskResponse()
     * from the provided speechText and repromptText. If isAskResponse is false, generates a 
     * 'tell response' via SpeechletResponse.newTellResponse() from the provided speechText. In that case, 
     * repromptText is not used. 
     * 
     * @param String speechText
     * @param String repromptText
     * @param boolean isAskResponse
     * @return SpeechletResponse
     */
    private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
            boolean isAskResponse) {
        SimpleCard card = new SimpleCard();
        card.setTitle("Patient Monitor");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        if (isAskResponse) {
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
            repromptSpeech.setText(repromptText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);

            return SpeechletResponse.newAskResponse(speech, reprompt, card);

        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }
    

}
