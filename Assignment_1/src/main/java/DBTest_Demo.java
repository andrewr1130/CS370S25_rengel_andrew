/***
Rubrics:
Level 1 (Passing grade): Demonstrate the ability to generate and run the stand-alone application in your own computer with mysql server installed in your own computer.

Level 2 (B-): Demonstrate the ability to set up mysql server in one machine and the ability to generate the stand-alone jar client application and run from a different machine while the two are in the same network.

Level 3 (B+): Demonstrate the ability to set up mysql server in one machine and the ability to generate the stand-alone jar client application and run from a different machine while the two are in the different networks.

Level 4 (A): Demonstrate the ability to set up mysql server in one machine and the ability to generate the stand-alone jar client application and run from a different machine while the two are in the different networks --- and while ssl connection.

Some useful commands
mysql> create user 'db_user'@'%' identified by 'pass';
mysql> grant usage on *.* to db_user@'%';
mysql> grant all privileges on db_repo.* to db_user@'%';  //remark: grant on all tables in db db_repo; i.e., db_repo.*

***/


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBTest_Demo {

	public DBTest_Demo (){
		
	}
	
	public int testconnection_mysql (int hr_offset) {        
		String connection_host = "18.191.33.21";
		Connection connect = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs;
        int flag = 0;
    	
		try {
		      // This will load the MySQL driver, each DB has its own driver
		      Class.forName("com.mysql.cj.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
	    		          //.getConnection("jdbc:mysql://149.4.223.xxx/studentdb?"+ "user=whatever&password=whatever");
        				    .getConnection("jdbc:mysql://" + connection_host + "/test?user=main&password=Batteryboy123!");
		      String qry1a = "SELECT CURDATE() + " + hr_offset;
		      
	    	  //comment out test message //System.out.println(qry1a);
	    	  preparedStatement = connect.prepareStatement(qry1a);
	    	  // "id, uid, create_time, token for id_management.id_logtime";
	    	  // Parameters start with 1
	    	  
	    	  ResultSet r1=preparedStatement.executeQuery();

	            if (r1.next())
	            {
	              String nt = r1.getString(1); 
	              //System.out.println(hr_offset + " hour(s) ahead of the system clock of mysql at 149.4.223.xxx is:" + nt);
	              System.out.println(hr_offset + " hour(s) ahead of the system clock of mysql at " + connection_host + " is:" + nt);
	            }
	            r1.close();
	            preparedStatement.close();
	    	  
	    	} catch (Exception e) {
	    		try {
					throw e;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		  	} finally {
			      if (preparedStatement != null) {
				        try {
							preparedStatement.close();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				  }	      

			      if (connect != null) {
			        try {
						connect.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			      }
		    }
		return flag;
	}
	
    	    
	public static void main(String[] args)
	{
		try
		{
			if (args.length != 1) {
				System.out.println("Usage: java -jar Ora_DBTest.jar <number_of_hr_offset>");
				System.out.println("Success returns errorlevel 0. Error return greater than zero.");
				System.exit(1);
			}

	        /* Print a copyright. */
	        System.out.println("Example for Oracle DB connection via Java");
	        System.out.println("Copyright: Bon Sy");
	        System.out.println("Free to use this at your own risk!");
	       
	    	DBTest_Demo DBConnect_instance = new DBTest_Demo();
	       
	    	System.out.println("\n");
	    	
	    	if (DBConnect_instance.testconnection_mysql(Integer.parseInt(args[0])) == 0) {
	            System.out.println("MYSQL Remote Connection Successful Completion");
	        } else {
	            System.out.println("mysql DB connection fail");
	        }
	    	
	       //DBConnect_instance.testconnection_mysql(Integer.parseInt(args[0]));
	        
		} 
		catch (Exception e){
			// probably error in input
			System.out.println("Hmmm... Looks like input error ....");
		}		
  }
	
	
}
