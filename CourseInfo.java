//----------------------------------------------------------------------
// CourseInfo.java
// Authors: Benjamin Musoke-Lubega (bpm3) Osita Ighodaro (ighodaro)
//----------------------------------------------------------------------

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.File;
import java.util.regex.Pattern;

public class CourseInfo
{
    private static final String DATABASE_NAME = "reg.sqlite";
    private static final String DEPT = "dept";
    private static final String COURSENUM = "coursenum";
    private static final String AREA = "area";
    private static final String TITLE = "title";

    
    public void CourseInfo()
    {

    }


    public String getClassID()
    {
        return classID;
    }

    public String getCourseInfo(String classID)
   {
       try
       {
        final String DATABASE_NAME = "reg.sqlite";
        String output = "";

        File databaseFile = new File(DATABASE_NAME);
            if (! databaseFile.isFile())
                System.err.println("regdetails: Database connection failed");

        if (!Pattern.matches("[0-9]*", classID))
        {
            System.err.println("regdetails: classid is not an integer");
        }

        Connection connection =
                DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);

            String stmtStr = "SELECT courses.courseid, days, starttime, endtime, bldg, roomnum, area, title, " +
                "descrip, prereqs " + 
                "FROM courses, classes " +
                "WHERE classes.courseid = courses.courseid " +
                "AND classid = ?";

            String stmtStr2 = "SELECT dept, coursenum FROM crosslistings, classes " + 
                "WHERE crosslistings.courseid = classes.courseid " +
                "AND classid = ? ORDER BY dept, coursenum";

            String stmtStr3 = "SELECT profname FROM profs, coursesprofs, classes " + 
                "WHERE coursesprofs.courseid = classes.courseid " +
                "AND coursesprofs.profid = profs.profid AND classid = ? " + 
                "ORDER BY profname";

            // Create a prepared statement and substitute values.
            PreparedStatement statement = 
                connection.prepareStatement(stmtStr);
            statement.setString(1, classID);

            PreparedStatement statement2 = 
                connection.prepareStatement(stmtStr2);
            statement2.setString(1, classID);

            PreparedStatement statement3 = 
                connection.prepareStatement(stmtStr3);
            statement3.setString(1, classID);

            ResultSet resultSet = statement.executeQuery();
            ResultSet resultSet2 = statement2.executeQuery();
            ResultSet resultSet3 = statement3.executeQuery();

            if (resultSet.isClosed())
            {
                System.err.println("regdetails: classid does not exist");
            }

            String courseID = resultSet.getString("courseid");
            String days = resultSet.getString("days");
            String startTime = resultSet.getString("starttime");
            String endTime = resultSet.getString("endtime");
            String bldg = resultSet.getString("bldg");
            String roomNum = resultSet.getString("roomnum");
            String area = resultSet.getString("area");
            String title = resultSet.getString("title");
            String descrip = resultSet.getString("descrip");
            String prereqs = resultSet.getString("prereqs");

            output += "\nCourse ID:\n" + courseID + "\n";    
            output += "\nDays:\n" + days + "\n";
            output += "\nStart Time:\n" + startTime + "\n";
            output += "\nEnd Time:\n" + endTime + "\n";
            output += "\nBuilding:\n" + bldg + "\n";
            output += "\nRoom Number:\n" + roomNum + "\n";

            output += "\nDepartment and Course Number:\n";
            while(resultSet2.next())
            {
                String dept = resultSet2.getString("dept");
                String courseNum = resultSet2.getString("coursenum");

                output += dept + " ";
                output += courseNum + "\n";
            }
            
            output += "\nDistribution Area: (if applicable)\n" + area + "\n";
            output += "\nTitle:\n" + title + "\n";
            output += "\nDescription:\n" + descrip + "\n";
            output += "\nPrerequisites:\n" + prereqs + "\n";
            output += "\nProfessor Names:\n";
            while(resultSet3.next())
            {
                String profName = resultSet3.getString("profname");
                output += profName + "\n";
            }

            connection.close();
            return output;
        }
        catch (Exception e) 
        {
            System.err.println("regdetails: database reg.sqlite not found");
            System.exit(1); 
        }
        return null;
    }

    public ArrayList getCourseBasic(String[] inputs)
    {
        ArrayList output = new ArrayList<>();

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(DEPT, 0);
        map.put(COURSENUM, 0);
        map.put(AREA, 0);
        map.put(TITLE, 0);

        // Identifies search queries
        String whereString = new String();
        
        // Read in queries
        for (int i = 0; i < inputs.length; i++)
        {
            String key = inputs[i].substring(1);
            
            // check validity of key
            if (map.containsKey(key))
            {
                if (map.get(key) == 0) map.put(key, 1);
                else 
                {
                    System.err.println("reg: duplicate key");
                    System.exit(1);
                }
            }
            else
            {
                System.err.println("reg: invalid key");
                System.exit(1);
            }
            i++;

            // checks for missing value
            if (i >= inputs.length) 
            {
                System.err.println("reg: missing value");
                System.exit(1);
            }

            String value = inputs[i];

            if (key.equals(DEPT)) 
            {
                value = value.toUpperCase();
                value = "\"" + value + "\"";
                whereString += " AND " + key + " = " + value;
            }
            else if (key.equals(COURSENUM))
            {
                whereString += " AND " + key + " = " + value;
            }
            else if (key.equals(AREA))
            {
                value = value.toUpperCase();
                value = "\"" + value + "\"";
                whereString += " AND " + key + " = " + value;
            }
            else if (key.equals(TITLE))
            {
                value = "%" + value + "%";
                value = "\"" + value + "\"";
                whereString += " AND " + key + " LIKE " + value;
            }
        }

        try
        {
            File databaseFile = new File(DATABASE_NAME);
            if (!databaseFile.isFile())
                throw new Exception("Database connection failed");

            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);

            String stmtStr = "SELECT classid, dept, coursenum, area, title  " + 
            "FROM courses, classes, crosslistings " +
            "WHERE courses.courseid = crosslistings.courseid " +
            "AND classes.courseid = courses.courseid " +
            "AND classes.courseid = crosslistings.courseid " +
            whereString.toString() +
            " ORDER BY dept, coursenum, classid;";

            PreparedStatement statement = connection.prepareStatement(stmtStr);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                String lineOfOutput;
                String classid = resultSet.getString("classid");
                String dept = resultSet.getString("dept");
                String coursenum = resultSet.getString("coursenum");
                String area = resultSet.getString("area");
                String title = resultSet.getString("title");

                lineOfOutput = new String(classid + "\t" + dept + "\t" + coursenum + 
                "\t" + area + "\t" + title);

                output.add(lineOfOutput.toString());
            }

            connection.close();
            return output;
        }
        catch (Exception e) 
        { 
            System.err.println("regdetails: database reg.sqlite not found"); 
            System.exit(1);
        }
    
        return null;
    }
}