package retoon.retoon_server.src.user;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import retoon.retoon_server.config.BaseException;
import retoon.retoon_server.config.BaseResponseStatus;
import retoon.retoon_server.config.secret.Secret;
import retoon.retoon_server.src.user.repository.UserRepository;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Autowired 역할
public class MailService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    public static final String verifyCode = CreateCode();

    /** 회원가입 인증 번호 메세지 생성 */
    private MimeMessage createMessage(String code, String email) throws Exception{
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("Re:Toon 회원가입 인증 번호입니다.");
        message.setText("이메일 인증코드 : " + code);
        message.setFrom(new InternetAddress(Secret.RECIPIENT, "Re:Toon"));

        return message;
    }

    /** 인증 코드가 담긴 메일을 전송 */
    public void sendMail(String code, String email) throws Exception{
        try{
            MimeMessage mimeMessage = createMessage(code, email);
            javaMailSender.send(mimeMessage);
        }
        catch(MailException mailException){
            mailException.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    /** 인증 코드 랜덤 생성 및 실제 전송 진행 */
    public String sendCertification(String email) throws BaseException {
        if(userRepository.existsByEmail(email)){
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }
        try{
            sendMail(verifyCode, email);
            return verifyCode;
        }
        catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    /** 인증 코드 랜덤 생성 */
    public static String CreateCode(){
        return UUID.randomUUID().toString().substring(0, 6);
    }

}
