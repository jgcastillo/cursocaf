package com.spontecorp.jsf.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emailer {

    private String sms;
    private String nombre;
    private String subject;
    private String para;
    private static final String de = "cursoscaf@gmail.com";
    private static final String password = "sponte2013";
    private String asunto;
    private String cuerpo;
    private String horario;
    private Properties props;
    private Session mailSession;
    private String vinculo;
    private String idPersona;
    private String idCurso;

    public Emailer() {
        setProperties();
    }

    public void setProperties() {
        props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        mailSession = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(de, password);
            }
        });
    }

    public void send() {
        try {
            Transport transport;
            transport = mailSession.getTransport();
            MimeMessage message = new MimeMessage(mailSession);
            message.setSubject(asunto);
            message.setFrom(new InternetAddress(de));
            setCuerpo();
            message.setText(cuerpo);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(para));

            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();

        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setCuerpo() {
        String parametros = "";
        parametros = parametros + "?email=" + para;
        parametros = parametros + "&idPersona=" + idPersona;
        parametros = parametros + "&idCurso=" + idCurso;
        vinculo = vinculo + parametros;
        cuerpo = "Estimado/a " + nombre + "\n"
                + "Usted se ha inscrito en el curso pautado para el día " + horario + "\n"
                + "Por favor entre el siguiente vínculo para confirmar su asistencia: "
                + vinculo;


    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPara() {
        return para;
    }

    public void setPara(String para) {
        this.para = para;
    }

    public String getDe() {
        return de;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public Session getMailSession() {
        return mailSession;
    }

    public void setMailSession(Session mailSession) {
        this.mailSession = mailSession;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getVinculo() {
        return vinculo;
    }

    public void setVinculo(String Vinculo) {
        this.vinculo = Vinculo;
    }

    public String getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(String idPersona) {
        this.idPersona = idPersona;
    }

    public String getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(String idCurso) {
        this.idCurso = idCurso;
    }
}
