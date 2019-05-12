## Assignment Planner - Java Final Project

Assignment Planner is an application that could potentially be used 
by a student in school to keep their assignments organized. There are
fields for class name, class code, assignment description, and due date.
The user is able to add, delete, and update assignments. User can also
export list of assignments to Excel and they can add assignments to their
Google Calendar.

You will not need any user names/passwords to run the program. Nor will you
need to download any 3rd party libraries. The needed dependencies are in the
pom.xml file. 

To add assignments to your Google calendar, you may need to allow Google 
Quickstart to access your Google calendars. A page will open in your browser and
will prompt you to do so the first time you click the "add to Google calendar" button. 

Known bugs or problems:
- When you click "add to Google calendar" for the first time and then when it asks you to 
"allow Quickstart to access your calendars", if you close the window in your browser 
instead of accepting, the program will freeze and you cannot click anywhere so you have 
to manually stop running the program. 

Program features:
- Can add and delete assignments from table
- Can update assignment description field, but cannot edit other fields
- Can sort columns in table by clicking on column headers
- Can export list of assignments to Excel spreadsheet
- Can add assignments to Google calendar 

