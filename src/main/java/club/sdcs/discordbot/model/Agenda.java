package club.sdcs.discordbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="agenda")
public class Agenda extends ClubEvent {
    private LocalDateTime dueDate;

    public Agenda(long id, String title, String url, LocalDateTime dueDate) {
        this.setId(id);
        this.setTitle(title);
        this.setUrl(url);
        this.dueDate = dueDate;
    }

    public Agenda() {}

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
