package dyb.data.crawl.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobInfoDto {
	private String jobName;
	private String jobCity;
	private String salaryRange;
	/** 工作经验 */
	private String experience;
	/** 学历 */
	private String education;
	private String jobDesc;
	private String companyId;
	/** 经度 */
	private double longitude;
	/** 纬度 */
	private double latitude;
	private String jobAddress;
}
