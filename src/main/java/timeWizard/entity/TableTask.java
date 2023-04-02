package timeWizard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name="table_tasks")
public class TableTask {
    @Id
    @NonNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(name="text")
    private String text;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "column_id")
    private TableColumn tableColumn;

}
