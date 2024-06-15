package club.sdcs.discordbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="agenda")
public class Agenda extends Auditable {
    @Id
    private long id;
    private String title;
    private String link;
    private LocalDateTime dueDate;

    public Agenda(long id, String title, String link, LocalDateTime dueDate) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.dueDate = dueDate;
    }

    public Agenda() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
