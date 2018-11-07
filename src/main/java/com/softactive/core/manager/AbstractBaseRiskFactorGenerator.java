package com.softactive.core.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.softactive.core.object.Base;
import com.softactive.core.object.CoreConstants;
import com.softactive.core.repository.AbstractDataService;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractBaseRiskFactorGenerator<R extends Base, I extends Base, RF extends Base> implements CoreConstants, Serializable {
	private static final long serialVersionUID = 3725128615568955598L;
	protected AbstractDataService<RF> rfs;
	protected AbstractDataService<I> is;
	protected AbstractDataService<R> rs;

	@Getter
	private int indicatorIndex = 0;
	@Getter
	private int indicatorCount = 0;
	@Setter
	private Integer progress = null;

	protected abstract RF getRiskFactor(R r, I i);
	
	public void start(List<I> indicators, List<R> regions) {
		for (R r : regions) {
			rs.save(r);
		}
		indicatorCount = indicators.size();
		for (I i : indicators) {
			for(RF rf:getRiskFactorsForIndicator(regions, i)) {
				rfs.save(rf);
			}
			is.save(i);
			indicatorIndex++;
		}
	}

	public void start(List<I> indicators, R r) {
		List<R> regions = new ArrayList<>();
		regions.add(r);
		start(indicators, regions);
	}

	public Integer getProgress() {
		if (progress == null) {
			progress = 0;
		}
		if(indicatorCount == 0) {
			return 0;
		}
		progress = 100 * indicatorIndex / indicatorCount;
		return progress;
	}
	protected List<RF> getRiskFactorsForIndicator(List<R> regions, I i) {
		List<RF> list = new ArrayList<>();
		for (R r : regions) {
			list.add(getRiskFactor(r, i));
		}
		return list;
	}
}
