/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import dao.MedicoJpaController;
import dto.Medico;
import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Quichiz
 */
@WebServlet(name = "LoginMedicoServlet", urlPatterns = {"/LoginMedicoServlet"})
public class LoginMedicoServlet extends HttpServlet {

   private MedicoJpaController medicoJpa;

    @Override
    public void init() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_ExamenRecuperacion_war_1.0-SNAPSHOTPU");
        medicoJpa = new MedicoJpaController(emf);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ndni = request.getParameter("ndni");
        String pass = request.getParameter("password");

        Medico medico = medicoJpa.findByNdni(ndni);

        response.setContentType("text/plain;charset=UTF-8");
        if (medico != null) {
            if (BCrypt.checkpw(pass, medico.getPassMedi())) {
                request.getSession().setAttribute("usuario", medico);
                response.getWriter().write("Login exitoso. Bienvenido Dr. " + medico.getNombMedi());
            } else {
                response.getWriter().write("Clave incorrecta");
            }
        } else {
            response.getWriter().write("Usuario no encontrado");
        }
    }

}
