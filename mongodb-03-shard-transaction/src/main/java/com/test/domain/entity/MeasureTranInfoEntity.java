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
@Document("t_measure_tran_info")
@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MeasureTranInfoEntity {

	@Id
	@ApiModelProperty("")
	String id;

	@ApiModelProperty("")
	String tranId;

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
	Double loadRate;

	@ApiModelProperty("")
	Double lowVoltageSideI;

	@ApiModelProperty("")
	Double lowVoltageSideImax;

	@ApiModelProperty("")
	Double lowVoltageSideLoadRate;

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
