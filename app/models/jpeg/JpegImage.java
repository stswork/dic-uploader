package models.jpeg;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@SequenceGenerator(name = "jpeg_image_seq", sequenceName = "jpeg_image_seq", initialValue = 1000)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JpegImage extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Constraints.Required
    @Lob
    public byte[] imageByteArray;

    @Formats.DateTime(pattern = "MM/dd/yy")
    private Timestamp created = new Timestamp(DateTime.now().toDate().getTime());

    public JpegImage() {
    }

    public JpegImage(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public static Finder<Long, JpegImage> find = new Finder<Long, JpegImage>(
            Long.class, JpegImage.class
    );
}
