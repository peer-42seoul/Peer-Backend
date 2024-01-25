package peer.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import peer.backend.dto.ContactUsRequest;
import peer.backend.entity.ContactUs;
import peer.backend.repository.ContactUsRepository;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactUsService {
    private final ContactUsRepository contactUsRepository;
    private static final String DELIMITER = "#$$#";
    private static final String SUBJECT = "문의 메일 : [ ";
    private static final String END = " ] received -";
    private static final String INIT = "문의 사항이 접수되었습니다.\n";

    private final JavaMailSender sender;

    @Transactional
    public void saveContactUs(ContactUsRequest data) {
        ContactUs targetData = ContactUs.builder()
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .email(data.getEmail())
                .companyAndSite(data.getCompany()
                    + ContactUsService.DELIMITER
                    + data.getCompanySite())
                .text(data.getText())
                .build();

        this.contactUsRepository.save(targetData);
    }


    @Scheduled(cron = "0 30 8 * * ?")
    public void mailToClient() {
        List<ContactUs> mailsForClient = this.contactUsRepository
                .findAllForClient();
        mailsForClient.forEach(m -> {
            try {
                SimpleMailMessage mail = this.makeClientInitMail(m);
                sender.send(mail);
                m.setEmailClientSent(true);
            } catch (Exception e) {
                m.setEmailClientSent(false);
            }
        });
        this.contactUsRepository.saveAll(mailsForClient);

    }

    @Scheduled(cron = "0 0 4 * * ?")
    public void mailToManeger() {
        List<ContactUs> mailsForManeger = this.contactUsRepository
                .findAllForManeger();
        mailsForManeger.forEach(m ->{
            try {
                SimpleMailMessage mail = this.notiMailForManager(m);
                sender.send(mail);
                m.setEmailManagementSent(true);
            } catch (Exception e) {
                m.setEmailManagementSent(false);
            }
        });
        this.contactUsRepository.saveAll(mailsForManeger);
    }

    private SimpleMailMessage makeClientInitMail(ContactUs targetData) {
        LocalDateTime korTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss");
        String formattedKoreanTime = korTime.format(formatter);

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(targetData.getEmail());
        mailMessage.setSubject("peer에 문의 사항이 접수되었습니다.");
        String body = "";
        body += "문의 접수 안내 메일\n\n";
        body += "문의 요청을 주셔서 감사합니다. 조속히 확인 후 연락 드리도록 하겠습니다.\n";
        body += "문의 요청 : " + targetData.getFirstName() + " " + targetData.getLastName();
        if (!targetData.getCompanyAndSite().isEmpty()) {
            body += "( " + targetData.getCompanyAndSite().replace(ContactUsService.DELIMITER, " / ");
            body += " )\n";
        }
        body += "문의 접수 일시 : " + formattedKoreanTime + '\n';
        body += '\n';
        body += "====================================\n\n";
        body += targetData.getText() + "\n\n";
        body += "====================================\n\n";
        body += "\n\n";
        body += "peer 운영팀 드림";

        mailMessage.setText(body);

        return mailMessage;
    }

    private SimpleMailMessage notiMailForManager(ContactUs targetData) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo("42peer@gmail.com");
        mailMessage.setSubject("문의 메일 : "
                + targetData.getFirstName()
                + " "
                + targetData.getLastName());
        String body = "";
        body += "문의 요청 : " + targetData.getFirstName() + " " + targetData.getLastName();
        if (!targetData.getCompanyAndSite().isEmpty()) {
            body += "( " + targetData.getCompanyAndSite().replace(ContactUsService.DELIMITER, " / ");
            body += " )\n";
        }
        body += "문의 접수 일시 : " + targetData.getCreatedAt() + '\n';
        body += '\n';
        body += "====================================\n\n";
        body += targetData.getText() + "\n\n";
        body += "====================================\n\n";

        mailMessage.setText(body);

        return mailMessage;
    }


}