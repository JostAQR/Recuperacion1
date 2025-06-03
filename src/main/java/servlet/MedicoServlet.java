/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlet;

import com.google.gson.Gson;
import dao.MedicoJpaController;
import dto.Medico;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Quichiz
 */
@WebServlet(name = "MedicoServlet", urlPatterns = {"/MedicoServlet"})
public class MedicoServlet extends HttpServlet {

    private MedicoJpaController medicoJpa;

    @Override
    public void init() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_ExamenRecuperacion_war_1.0-SNAPSHOTPU");
        medicoJpa = new MedicoJpaController(emf);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Medico> lista = medicoJpa.findMedicoEntities();

        // Excluir el campo passMedi
        List<Map<String, Object>> listaLimpia = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Medico m : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("codiMedi", m.getCodiMedi());
            json.put("ndniMedi", m.getNdniMedi());
            json.put("appaMedi", m.getAppaMedi());
            json.put("apmaMedi", m.getApmaMedi());
            json.put("nombMedi", m.getNombMedi());
            json.put("fechNaciMedi", sdf.format(m.getFechNaciMedi()));
            json.put("logiMedi", m.getLogiMedi());
            listaLimpia.add(json);
        }

        response.setContentType("application/json;charset=UTF-8");
        new Gson().toJson(listaLimpia, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int codi = Integer.parseInt(request.getParameter("codiMedi"));
            String appa = request.getParameter("appaMedi");
            String apma = request.getParameter("apmaMedi");
            String nomb = request.getParameter("nombMedi");
            String fecha = request.getParameter("fechNaciMedi");
            String login = request.getParameter("logiMedi");

            Medico medico = medicoJpa.findMedico(codi);
            if (medico != null) {
                medico.setAppaMedi(appa);
                medico.setApmaMedi(apma);
                medico.setNombMedi(nomb);
                medico.setFechNaciMedi(java.sql.Date.valueOf(fecha)); // convierte yyyy-MM-dd a Date
                medico.setLogiMedi(login);
                // NO se toca el passMedi

                medicoJpa.edit(medico);
                response.getWriter().write("Datos actualizados correctamente.");
            } else {
                response.getWriter().write("Médico no encontrado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error al actualizar: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int codi = Integer.parseInt(request.getParameter("codiMedi"));
            medicoJpa.destroy(codi);
            response.getWriter().write("Médico eliminado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error al eliminar: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Leer el cuerpo manualmente (porque no hay parámetros como en POST)
            StringBuilder jsonBuffer = new StringBuilder();
            String linea;
            while ((linea = request.getReader().readLine()) != null) {
                jsonBuffer.append(linea);
            }

            // Parsear JSON
            Gson gson = new Gson();
            Map<String, String> datos = gson.fromJson(jsonBuffer.toString(), Map.class);

            String ndni = datos.get("ndniMedi");
            String appa = datos.get("appaMedi");
            String apma = datos.get("apmaMedi");
            String nomb = datos.get("nombMedi");
            String fecha = datos.get("fechNaciMedi");
            String login = datos.get("logiMedi");
            String pass = datos.get("passMedi");

            // Hashear contraseña
            String passHash = org.mindrot.jbcrypt.BCrypt.hashpw(pass, org.mindrot.jbcrypt.BCrypt.gensalt());

            Medico nuevo = new Medico();
            nuevo.setNdniMedi(ndni);
            nuevo.setAppaMedi(appa);
            nuevo.setApmaMedi(apma);
            nuevo.setNombMedi(nomb);
            nuevo.setFechNaciMedi(java.sql.Date.valueOf(fecha));
            nuevo.setLogiMedi(login);
            nuevo.setPassMedi(passHash);

            medicoJpa.create(nuevo);

            response.getWriter().write("Médico registrado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error al registrar: " + e.getMessage());
        }
    }

}
