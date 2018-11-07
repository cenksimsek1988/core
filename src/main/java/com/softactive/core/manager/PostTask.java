package com.softactive.core.manager;

import java.util.Map;

public interface PostTask {

	void onPost(Map<String, Object> sharedParams);
}
