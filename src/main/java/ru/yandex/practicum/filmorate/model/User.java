package ru.yandex.practicum.filmorate.model;

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
    private int id;
    @NotNull(message = "Can't be null.")
    @Email(message = "Invalid email format.")
    private String email;
    @NotNull(message = "Can't be null.")
    @NotBlank(message = "Can't be blank.")
    @Pattern(regexp = "[\\S]{0,}", message = "Must not contain spaces.")
    private String login;
    private String name;
    @NotNull(message = "Can't be null.")
    @Past(message = "It can't be later than the current day.")
    private LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();

    public void addFriend(Integer friendId) {
        friends.add(friendId);
    }

    public void deleteFriend(Integer friendId) {
        if (!friends.remove(friendId)) {
            throw new NotFoundException(String.format("Friend with id = %d not found", friendId));
        }
    }
}