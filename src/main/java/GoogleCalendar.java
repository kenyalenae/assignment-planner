/*
Class to add assignment and due date to Google Calendar
This is based off of Clara's example below:
https://github.com/claraj/hello_google_calendar/blob/master/src/main/java/com/company/GoogleCalendar.java
 */

/*
Notes from Clara's example (for my own reference):
 * Does Google Calendar authentication,
 * Creates a new Calendar with a particular name for the user, if that calendar does not already exist
 * Adds new events to that named Google Calendar. (One user can have multiple calendars).
 * See sample application and docs for other Calendar operations
 * https://github.com/google/google-api-java-client-samples/blob/master/calendar-cmdline-sample/src/main/java/com/google/api/services/samples/calendar/cmdline/View.java
 * Make sure you add a file with the JSON credentials for your own application in. Replace the src/main/resources/client_secret.json with your own copy
*/

// import statements that I got from Clara's example
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.services.calendar.model.*;

import java.io.IOException;
import java.io.InputStreamReader;
//import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class GoogleCalendar {

    private static final String APPLICATION_NAME = "AssignmentPlanner";

    private static final String CALENDAR_NAME = "Assignments";

    // directory to store user credentials
    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");

    // global instance of the {DataStoreFactory}
    private static FileDataStoreFactory dataStoreFactory;

    // global instance of the HTTP transport
    private static HttpTransport httpTransport;

    // global instance of the JSON factory
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static com.google.api.services.calendar.Calendar client;

    // authorizes the installed application to access users protected data
    private static Credential authorize() throws Exception {

        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(GoogleCalendar.class.getResourceAsStream("/client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
            || clientSecrets.getDetails().getClientSecret().startsWith("Enter")) {
            System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
                    + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);

        }

        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory).build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(("user"));

    }

    public static void addEvent(String eventName) {

        try {

            // initialize the transport
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // initialize the data store factory
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

            // authorization
            Credential credential = authorize();

            // set up global calendar instance
            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

            // get the calendar the app will use. this will create a calendar if it does not already exist
            Calendar appCalendar = getAppCalendar();

            // add new event to the calendar
            add(eventName, appCalendar);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // check if calendar already exists, if it does then return it
    // if doesnt exist, create new calendar
    private static Calendar getAppCalendar() throws IOException {

        // TODO
        // get list of calendars
        CalendarList calendarList = client.calendarList().list().execute();

        // check if a calendar exists with the app calendar name
        for (CalendarListEntry calendarListEntry : calendarList.getItems()) {

            // check if calendar has the same summary name
            if (calendarListEntry.getSummary().equals(CALENDAR_NAME)) {
                // calendar exists, get this calendars unique id
                String calendarId = calendarListEntry.getId();
                // anf fetch the Calendar object and return it
                return client.calendars().get(calendarId).execute();
            }
        }

        // a calendar with this name does not already exist
        // create new calendar and return it
        Calendar newAppCalendar = addCalendar();
        return newAppCalendar;

    }

    // add new Calendar
    private static Calendar addCalendar() throws IOException {

        // TODO
        Calendar entry = new Calendar();
        entry.setSummary(CALENDAR_NAME);
        Calendar result = client.calendars().insert(entry).execute();
        return result;

    }

    // add event and calendar
    private static void add(String eventName, Calendar calendar) throws IOException {

        // TODO
        Event event = newEvent(eventName);
        System.out.println("Event to add to calendar: " + event);
        Event result = client.events().insert(calendar.getId(), event).execute();
        System.out.println("Result of adding event to calendar: " + result);
    }

    // add new event to calendar
    private static Event newEvent(String eventName) {

        // TODO
        Event event = new Event();
        event.setSummary(eventName);
        Date startDate =  new Date();
        Date endDate = new Date(startDate.getTime() + 3600000);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(end));
        return event;

    }


}
