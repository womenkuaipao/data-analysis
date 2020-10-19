package dyb.data.crawl.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDto {
	/** 公司id */
	private String companyId;
	private String name;
	/** 公司性质 */
	private String industry;
	/** 融资 */
	private String finace;
	/** 公司规模，员工数目 */
	private String employees;
	/** 福利待遇 */
	private String welfare;
	/** 成立时间 */
	private Date createTime;
	/** 地址 */
	private String address;
	/** 经度 */
	private double longitude;
	/** 纬度 */
	private double latitude;
}
