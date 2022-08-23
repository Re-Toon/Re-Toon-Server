package retoon.retoon_server.src.login.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuthRequestDto {
    String email;
    String authToken;
}
