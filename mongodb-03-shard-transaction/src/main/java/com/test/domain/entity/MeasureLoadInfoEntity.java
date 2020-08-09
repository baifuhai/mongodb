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
@Document("t_measure_load_info")
@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeasureLoadInfoEntity {

	@Id
	@ApiModelProperty("")
	String id;

	@ApiModelProperty("")
	String loadId;

	@ApiModelProperty("")
	Double imax;

	@ApiModelProperty("")
	Double rPl;

	@ApiModelProperty("")
	Double rQl;

	@ApiModelProperty("")
	Double rI;

	@ApiModelProperty("")
	Double loadRate;

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
