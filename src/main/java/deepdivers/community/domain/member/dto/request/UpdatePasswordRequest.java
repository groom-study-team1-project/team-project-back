package deepdivers.community.domain.member.dto.request;

public record UpdatePasswordRequest(String currentPassword, String newPassword) {
}
