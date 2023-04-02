package timeWizard.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@NoArgsConstructor
@Entity
@Table(name="calendar_tasks")
public class CalendarTask {
	@Id
	@NonNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String title;
	private String definition;
	@NonNull
	@Column(name="user_email")
	private String email;

	@NonNull
	@Column(name="start_date")
	private Date startDate;
	@NonNull
	@Column(name="end_date")
	private Date endDate;
	
	private Boolean completed;

	@NonNull
	@Column(name="color")
	private String colorInHex;

	@NonNull
	@Column(name="frequency")
	private int frequency;
	@NonNull
	@Column(name="time_unit")
	private String timeUnit;

}
