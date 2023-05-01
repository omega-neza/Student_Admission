/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import dao.AdmissionformDAO;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Admissionform;
import java.util.Date;
/*import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;*/
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

/**
 *
 * @author omega
 */
@WebServlet(name = "AdmissionformServlet", urlPatterns = {"/AdmissionformServlet"})
@MultipartConfig(maxFileSize = 16177215) // 16 MB
public class AdmissionformServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AdmissionformServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdmissionformServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    HttpSession session = request.getSession();
           Admissionform admission= new Admissionform();
            admission.setFirstname(request.getParameter("firstname"));
            admission.setLastname(request.getParameter("lastname"));
            

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dob = null;
        try {
            dob = dateFormat.parse(request.getParameter("dob"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        java.sql.Date sqlDate = new java.sql.Date(dob.getTime());
        admission.setDob(sqlDate);
    
            admission.setGender(request.getParameter("gender"));
            admission.setPhonenumber(request.getParameter("phonenumber"));
            admission.setId(request.getParameter("id"));
            admission.setEmail(request.getParameter("email"));
           
            admission.setAddress(request.getParameter("address"));
            
            
            // handle image file upload
        Part imagePart = request.getPart("image");
        if (imagePart != null && imagePart.getSize() > 0) {
            InputStream imageInputStream = imagePart.getInputStream();
            byte[] imageBytes = new byte[(int) imagePart.getSize()];
            imageInputStream.read(imageBytes);
            admission.setImage(imageBytes);
        }
        
        // handle PDF file upload
        Part pdfPart = request.getPart("pdf");
        if (pdfPart != null && pdfPart.getSize() > 0) {
            InputStream pdfInputStream = pdfPart.getInputStream();
            byte[] pdfBytes = new byte[(int) pdfPart.getSize()];
            pdfInputStream.read(pdfBytes);
            admission.setPdf(pdfBytes);
        }
            
        AdmissionformDAO dao = new AdmissionformDAO();
        dao.createAdmissionform(admission);
        
        // set session attributes
    session.setAttribute("loggedIn", true);
    session.setAttribute("email", request.getParameter("email"));
    
    // create email properties
Properties properties = new Properties();
properties.put("mail.smtp.host", "smtp.gmail.com");
properties.put("mail.smtp.port", "587");
properties.put("mail.smtp.auth", "true");
properties.put("mail.smtp.starttls.enable", "true");

// create mail session
Session mailSession = Session.getInstance(properties, new javax.mail.Authenticator() {
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("your email", "your password");
    }
});

try {
    // create email message
    Message message = new MimeMessage(mailSession);
    message.setFrom(new InternetAddress("your email"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(request.getParameter("email")));
    message.setSubject("Admission Submission Confirmation");
    message.setText("Dear " + request.getParameter("firstname") + " " + request.getParameter("lastname") + ",\n\nThank you for submitting your admission form. Your application has been received and is currently being reviewed. You will receive an update on your admission status in the coming days.\n\nBest regards,\nAdmission Office");

    // send email
    Transport.send(message);

   // System.out.println("Email sent successfully.");
    response.sendRedirect("Admission_success.html");
} catch (MessagingException e) {
    throw new RuntimeException(e);
}

        
        
            
    }


    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
