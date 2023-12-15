package peer.backend.batch;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import peer.backend.mongo.repository.UserTrackingRepository;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final UserTrackingRepository userTrackingRepository;

    @Bean
    public Job walletAdjustment() {
        return jobBuilderFactory.get("walletAdjustment")
            .start(integratingActionWallet())
            .build();
    }

    @Bean
    public Step integratingActionWallet() {
        return stepBuilderFactory.get("integratingActionWallet")
            .tasklet((contribution, chunkContext) -> {
                // 정산 로직
                log.info(">>>>> This is integratingActionWallet");
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
