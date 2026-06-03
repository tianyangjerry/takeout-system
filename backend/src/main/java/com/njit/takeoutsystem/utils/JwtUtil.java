package com.njit.takeoutsystem.utils;

import com.njit.takeoutsystem.common.BusinessException;
import com.njit.takeoutsystem.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtUtil {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expire-hours}")
    private long expireHours;

    public String generateToken(User user) {
        long expireAt = Instant.now().plusSeconds(expireHours * 3600).getEpochSecond();
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"userId\":" + user.getId()
                + ",\"username\":\"" + escape(user.getUsername()) + "\""
                + ",\"role\":\"" + escape(user.getRole()) + "\""
                + ",\"exp\":" + expireAt + "}";
        String headerPart = encode(header);
        String payloadPart = encode(payload);
        return headerPart + "." + payloadPart + "." + sign(headerPart + "." + payloadPart);
    }

    public Long parseUserId(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new BusinessException(401, "token 无效");
        }

        String content = parts[0] + "." + parts[1];
        if (!sign(content).equals(parts[2])) {
            throw new BusinessException(401, "token 无效");
        }

        String payload = new String(DECODER.decode(parts[1]), StandardCharsets.UTF_8);
        long exp = Long.parseLong(extractNumber(payload, "exp"));
        if (Instant.now().getEpochSecond() > exp) {
            throw new BusinessException(401, "token 已过期");
        }
        return Long.parseLong(extractNumber(payload, "userId"));
    }

    public String resolveToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(401, "请先登录");
        }
        return authorizationHeader.substring("Bearer ".length()).trim();
    }

    private String encode(String value) {
        return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return ENCODER.encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new BusinessException(500, "token 签名失败");
        }
    }

    private String extractNumber(String json, String key) {
        String marker = "\"" + key + "\":";
        int start = json.indexOf(marker);
        if (start < 0) {
            throw new BusinessException(401, "token 无效");
        }
        start += marker.length();
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }
        return json.substring(start, end);
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
