package com.hocztms.utils;

import com.hocztms.commons.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JavaMailSenderImpl javaMailSenderImpl;

    private static final String from = "1107578936@qq.com";
    private static final String team= "Tquest";

    /*
    发送找回密码文件
     */
    public void sendEmail(Email email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setSubject(team+":" + email.getSubject());
        messageHelper.setFrom(from);
        messageHelper.setTo(email.getTo());
        messageHelper.setText("<h1>亲爱的用户:</h1><br/><p>"+email.getContent() + "</p>", true);
        javaMailSender.send(messageHelper.getMimeMessage());
    }

    /*
    发送注册邮箱文件
     */
//    public void sendCodeEmail(Email email) {
//        MimeMessagePreparator messagePreparator = message -> {
//            message.setFrom(senderAdress);
//            message.setRecipients(Message.RecipientType.TO, email.getAddress());
//            message.setSubject(email.getTheme());
//            message.setText("<html><body>"+"<br>"
//                    +"您本次注册验证码为 "   +"<br>"
//                    + email.getSecret()
//                    + "<br>" + "from :" + sendername
//                    + "</body></html>");
//        };
//        try {
//            this.javaMailSender.send(messagePreparator);
//        } catch (MailException e) {
//            throw new RuntimeException("邮箱格式不正确");
//        }
//    }

    /*
    发送一般通知文件
     */
//    public void sendEmail(Email email) {
//        MimeMessagePreparator messagePreparator = message -> {
//            message.setFrom(senderAdress);
//            message.setRecipients(Message.RecipientType.TO, email.getAddress());
//            message.setSubject(email.getTheme());
//            message.setText("<html><body>"+"<br>"
//                    +"您好: " + email.getReceiver() + "<br>"
//                    + email.getMsg()
//                    + "<br>" + "from :" + sendername
//                    + "</body></html>");
//        };
//        try {
//            this.javaMailSender.send(messagePreparator);
//        } catch (MailException e) {
//            throw new RuntimeException("邮箱格式不正确");
//        }
//    }



//    public static Email generateNotifyEmail(Users users, String msg){
//        return new Email(users.getUsername(),users.getEmail(),"通知",msg,new Date(),null);
//    }
}
