package peer.backend.oauth;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import peer.backend.entity.user.PeerOperation;
import peer.backend.entity.user.User;
import peer.backend.entity.user.UserAchievement;
import peer.backend.entity.user.UserLink;
import peer.backend.entity.user.UserPushKeyword;
import peer.backend.oauth.enums.LoginStatus;

@Getter
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;
    private LoginStatus loginStatus;

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public PrincipalDetails(User user, Map<String, Object> attributes, LoginStatus loginStatus) {
        this.user = user;
        this.attributes = attributes;
        this.loginStatus = loginStatus;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public LocalDate getBirthday() {
        return user.getBirthday();
    }

    public boolean isAlarm() {
        return user.isAlarm();
    }

    public String getPhone() {
        return user.getPhone();
    }

    public String getAddress() {
        return user.getAddress();
    }

    public String getImageUrl() {
        return user.getImageUrl();
    }

    public boolean isCertification() {
        return user.isCertification();
    }

    public String getCompany() {
        return user.getCompany();
    }

    public String getIntroduce() {
        return user.getIntroduce();
    }

    public Long getPeerLevel() {
        return user.getPeerLevel();
    }

    public String getRepresentAchievement() {
        return user.getRepresentAchievement();
    }

    public List<UserPushKeyword> getUserPushKeywords() {
        return user.getUserPushKeywords();
    }

    public PeerOperation getPeerOperation() {
        return user.getPeerOperation();
    }

    public List<UserAchievement> getUserAchievements() {
        return user.getUserAchievements();
    }

    public List<UserLink> getUserLinks() {
        return user.getUserLinks();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
