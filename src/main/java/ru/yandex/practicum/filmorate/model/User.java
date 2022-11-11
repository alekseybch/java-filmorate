package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Integer id;
    @NotNull(message = "Can't be null.")
    @Size(max = 254, message = "Must not be more than 254 characters.")
    @Email(message = "Invalid format.")
    private String email;
    @NotBlank(message = "Can't be null or blank.")
    @Size(max = 20, message = "Must not be more than 20 characters.")
    @Pattern(regexp = "[\\S]{0,}", message = "Must not contain spaces.")
    private String login;
    @Size(max = 70, message = "Must not be more than 70 characters.")
    private String name;
    @NotNull(message = "Can't be null.")
    @Past(message = "It can't be later than the current day.")
    private LocalDate birthday;
    @JsonIgnore
    private final Set<Integer> friends = new HashSet<>();

    public void addFriend(Integer friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Integer friendId) {
        if (!friends.remove(friendId)) {
            throw new NotFoundException(String.format("Friend with id = %d not found", friendId));
        }
    }

    public void checkName() {
        if (getName() == null || getName().isBlank()) {
            setName(getLogin());
        }
    }
}