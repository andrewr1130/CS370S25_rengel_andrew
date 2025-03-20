package edu.cs;

import java.io.*;
import java.sql.*;
import java.util.Scanner;


import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/FileUploadServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*10, 	// 10 MB 
               maxFileSize=1024*1024*100,      	// 100 MB
               maxRequestSize=1024*1024*110)   	// 110 MB
public class FileUploadServlet extends HttpServlet {

  private static final long serialVersionUID = 205242440643911308L;
 
  /**
   * Directory where uploaded files will be saved, its relative to
   * the web application directory.
   */
  private static final String UPLOAD_DIR = "uploads";
  private static final String connection_host = "18.191.33.21";
  
  protected void doPost(HttpServletRequest request,
          HttpServletResponse response) throws ServletException, IOException {
      // gets absolute path of the web application
      String applicationPath = request.getServletContext().getRealPath("");

      // constructs path of the directory to save uploaded file
      String uploadFilePath = applicationPath + File.separator + UPLOAD_DIR;
      
      PrintWriter out = response.getWriter();

      // creates the save directory if it does not exists
      File fileSaveDir = new File(uploadFilePath);
      if (!fileSaveDir.exists()) {
          fileSaveDir.mkdirs();
      }
      System.out.println("Upload File Directory="+fileSaveDir.getAbsolutePath());
      
      String fileName = "";
      InputStream fileInputStream = null;
      long fileSize = 0;
      //Get all the parts from request and write it to the file on server
      for (Part part : request.getParts()) {
          fileName = getFileName(part);
          //Checking size of file
          if (!fileName.isEmpty()) {
              fileSize = part.getSize();
              
              //Reject File Greater than 100MB
              if (fileSize > 1024 * 1024 * 100) { // 100MB limit
                  response.setContentType("text/html");
                  out.println("<h3>File upload failed: " + fileName + " is too large! (Max: 100MB)</h3>");
                  return; // Stop processing
              }
              fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
              part.write(uploadFilePath + File.separator + fileName);
              fileInputStream = part.getInputStream();
          }     
      }
      
      if (fileName.isEmpty()) {
    	  out.println("<h3>No file uploaded.</h3>");
    	  return;
      }
      
    String message = "Result";
    String content = new Scanner(new File(uploadFilePath + File.separator + fileName)).useDelimiter("\\Z").next();      
    response.getWriter().write(message + "<BR>" + content);
    
    
    boolean success = storeFileInDatabase(fileName, fileInputStream);
    
    if (success) {
    	out.println("<h3>File uploaded successfully and stored in the database!</h3>"); 	
    }else {
    	out.println("<h3>File upload failed. </h3>");
    }
    
    
      
      //request.setAttribute("message", "File uploaded successfully!");
      //getServletContext().getRequestDispatcher("/response.jsp").forward(
      //        request, response);
      
      //Below is added for parsing EHR
		//DecodeCCDA parsed = new DecodeCCDA(uploadFilePath + File.separator + fileName);
		//writeToResponse(response, parsed.getjson());

    
      
  }
  
  private boolean storeFileInDatabase(String fileName, InputStream fileContent) {
	  String sql = "iNSERT INTO uploaded_files (file_name, file_data) VALUES (?, ?)";
	  try {
		  Class.forName("com.mysql.cj.jdbc.Driver");
		  Connection conn = DriverManager.getConnection("jdbc:mysql://" + connection_host + "/file_upload_db?user=main&password=Batteryboy123!");
		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.setString(1, fileName);
		stmt.setBlob(2,  fileContent);
		int rows = stmt.executeUpdate();
		return rows > 0;
		
	  } catch (Exception e) {
  			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
  			return false;
	  	}
}
  

  /**
   * Utility method to get file name from HTTP header content-disposition
   */
  private String getFileName(Part part) {
      String contentDisp = part.getHeader("content-disposition");
      System.out.println("content-disposition header= "+contentDisp);
      String[] tokens = contentDisp.split(";");
      for (String token : tokens) {
          if (token.trim().startsWith("filename")) {
              return token.substring(token.indexOf("=") + 2, token.length()-1);
          }
      }
      return "";
  }
  
  
	@SuppressWarnings("unused")
	private void writeToResponse(HttpServletResponse resp, String results) throws IOException {
		PrintWriter writer = new PrintWriter(resp.getOutputStream());
		resp.setContentType("text/plain");

		if (results.isEmpty()) {
			writer.write("No results found.");
		} else {
			writer.write(results);
		}
		writer.close();
	}	
	
	
}
