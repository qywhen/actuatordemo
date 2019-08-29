package com.wisd.actuatordemo.utils

import com.wisd.actuatordemo.bean.SysUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.CompressionCodecs
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap

/**
 * @author: JoeTao* createAt: 2018/9/14
 */
@Component
class JwtUtils {
    static final String ROLE_REFRESH_TOKEN = "ROLE_REFRESH_TOKEN"

    private static final String CLAIM_KEY_USER_ID = "user_id"
    private static final String CLAIM_KEY_AUTHORITIES = "scope"

    private Map<String, String> tokenMap = new ConcurrentHashMap<>(32)


    private String secret = '123456'


    private Long access_token_expiration = 3600


    private Long refresh_token_expiration = 3600

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256

    SysUser getUserFromToken(String token) {
        SysUser userDetail
        try {
            final Claims claims = getClaimsFromToken(token)
            def userId = getUserIdFromToken(token)
            String username = claims.getSubject()
            String roleName = claims.get(CLAIM_KEY_AUTHORITIES).toString()
            def list = JsonUtil.parse2List(roleName)
            userDetail = new SysUser(id: userId, username: username, authorities: list.collect({
                new SimpleGrantedAuthority(it)
            }))
        } catch (Exception e) {
            userDetail = null
        }
        return userDetail
    }

    String getUserIdFromToken(String token) {
        String userId
        try {
            final Claims claims = getClaimsFromToken(token)
            userId = Long.parseLong(String.valueOf(claims.get(CLAIM_KEY_USER_ID)))
        } catch (e) {
            userId = null
        }
        userId
    }

    String getUsernameFromToken(String token) {
        String username
        try {
            final Claims claims = getClaimsFromToken(token)
            username = claims.getSubject()
        } catch (e) {
            username = null
        }
        return username
    }

    Date getCreatedDateFromToken(String token) {
        Date created
        try {
            final Claims claims = getClaimsFromToken(token)
            created = claims.getIssuedAt()
        } catch (e) {
            created = null
        }
        return created
    }

    String generateAccessToken(SysUser userDetail) {
        Map<String, Object> claims = generateClaims(userDetail)
        def array = authoritiesToArray(userDetail.getAuthorities())
        claims.put(CLAIM_KEY_AUTHORITIES, JsonUtil.toJson(array))
        generateAccessToken(userDetail.getUsername(), claims)
    }

    Date getExpirationDateFromToken(String token) {
        Date expiration
        try {
            final Claims claims = getClaimsFromToken(token)
            expiration = claims.getExpiration()
        } catch (e) {
            expiration = null
        }
        return expiration
    }

    Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token)
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && (!isTokenExpired(token))
    }

    String refreshToken(String token) {
        String refreshedToken
        try {
            final Claims claims = getClaimsFromToken(token)
            refreshedToken = generateAccessToken(claims.getSubject(), claims)
        } catch (e) {
            refreshedToken = null
        }
        return refreshedToken
    }


    Boolean validateToken(String token, UserDetails userDetails) {
        SysUser userDetail = (SysUser) userDetails
        final def userId = getUserIdFromToken(token)
        final String username = getUsernameFromToken(token)
//        final Date created = getCreatedDateFromToken(token)
        return (userId == userDetail.getId() && username == userDetail.getUsername() && !isTokenExpired(token)
//                && !isCreatedBeforeLastPasswordReset(created, userDetail.getLastPasswordResetDate())
        )
    }

    String generateRefreshToken(SysUser userDetail) {
        Map<String, Object> claims = generateClaims(userDetail)
        // 只授于更新 token 的权限
        def roles = authoritiesToArray(userDetail.authorities)
        claims.put(CLAIM_KEY_AUTHORITIES, JsonUtil.toJson(roles))
        return generateRefreshToken(userDetail.getUsername(), claims)
    }

    void putToken(String userName, String token) {
        tokenMap.put(userName, token)
    }

    void deleteToken(String userName) {
        tokenMap.remove(userName)
    }

    boolean containToken(String userName, String token) {
        if (userName != null && tokenMap.containsKey(userName) && tokenMap.get(userName).equals(token)) {
            return true
        }
        return false
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody()
        } catch (Exception e) {
            claims = null
        }
        return claims
    }

    private Date generateExpirationDate(long expiration) {
        return new Date(System.currentTimeMillis() + expiration * 1000)
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token)
        return expiration.before(new Date())
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset))
    }

    private Map<String, Object> generateClaims(SysUser sysUser) {
        Map<String, Object> claims = new HashMap<>(16)
        claims.put(CLAIM_KEY_USER_ID, sysUser.getId())
        return claims
    }

    String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, access_token_expiration)
    }

    private List authoritiesToArray(Collection<? extends GrantedAuthority> authorities) {
        List<String> list = new ArrayList<>()
        for (GrantedAuthority ga : authorities) {
            list.add(ga.getAuthority())
        }
        return list
    }


    String generateRefreshToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, refresh_token_expiration)
    }


    private String generateToken(String subject, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate(expiration))
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SIGNATURE_ALGORITHM, secret)
                .compact()
    }

}
