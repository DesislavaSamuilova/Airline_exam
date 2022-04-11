package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "planes")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlaneRootSeedDto {

    @XmlElement(name = "plane")
    private List<PlanesDto> planes;

    public List<PlanesDto> getPlanes() {
        return planes;
    }

    public void setPlanes(List<PlanesDto> planes) {
        this.planes = planes;
    }
}