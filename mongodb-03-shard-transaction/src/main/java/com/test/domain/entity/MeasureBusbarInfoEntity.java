package com.test.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ApiModel("")
@Document("t_measure_busbar_info")
@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeasureBusbarInfoEntity {

	@Id
	@ApiModelProperty("")
	String id;

	@ApiModelProperty("")
	String busbarId;

	@ApiModelProperty("")
	Double vUp;

	@ApiModelProperty("")
	Double vDown;

	@ApiModelProperty("")
	Double rVm;

	@ApiModelProperty("")
	Double rVa;

	@ApiModelProperty("")
	Double overLimitRate;

	@ApiModelProperty("")
	Integer state;

	@ApiModelProperty("")
	Integer state2;

	@ApiModelProperty("")
	Long durationSeconds;

	@ApiModelProperty("")
	String duration;

	@ApiModelProperty("")
	String occurTime;

	@ApiModelProperty("")
	Integer occurTimeYear;

	@ApiModelProperty("")
	Integer occurTimeMonth;

	@ApiModelProperty("")
	String occurTimeWeek;

	@ApiModelProperty("")
	Integer occurTimeDay;

	@ApiModelProperty("")
	String occurTimeHourMinute;

}
