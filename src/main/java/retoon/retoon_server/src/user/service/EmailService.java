package retoon.retoon_server.src.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.secret.Secret;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@EnableAsync
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    // 메일을 보내는데 전송하는 동안 기다리게 되어 블록 상태에 놓일 수 있으므로 비동기로 처리
    @Async
    // 회원가입 인증 번호 메세지 생성 및 전송
    public void send(String email, String authToken) throws Exception{
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("Re:Toon 회원가입 인증 번호입니다.");
        message.setText("이메일 인증코드 : " + authToken);
        message.setFrom(new InternetAddress(Secret.RECIPIENT, "Re:Toon"));

        javaMailSender.send(message);
    }
}
