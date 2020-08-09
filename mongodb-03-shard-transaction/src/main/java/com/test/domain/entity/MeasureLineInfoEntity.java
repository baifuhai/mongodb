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
@Document("t_measure_line_info")
@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeasureLineInfoEntity {

	@Id
	@ApiModelProperty("")
	String id;

	@ApiModelProperty("")
	String lineId;

	@ApiModelProperty("")
	Double imax;

	@ApiModelProperty("")
	Double rPij;

	@ApiModelProperty("")
	Double rPji;

	@ApiModelProperty("")
	Double rQij;

	@ApiModelProperty("")
	Double rQji;

	@ApiModelProperty("")
	Double rIij;

	@ApiModelProperty("")
	Double rIji;

	@ApiModelProperty("")
	Double load;

	@ApiModelProperty("")
	Double i;

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
