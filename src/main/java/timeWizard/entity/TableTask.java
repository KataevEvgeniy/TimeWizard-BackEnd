package timeWizard.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name="table_tasks")
public class TableTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "column_id")
    private TableColumn tableColumn;

}
