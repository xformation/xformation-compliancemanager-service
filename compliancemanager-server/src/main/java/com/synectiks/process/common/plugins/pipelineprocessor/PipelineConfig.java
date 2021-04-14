/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor;

import com.github.joschi.jadconfig.Parameter;
import com.synectiks.process.server.plugin.PluginConfigBean;

public class PipelineConfig implements PluginConfigBean {

    @Parameter("cached_stageiterators")
    private boolean cachedStageIterators = true;

    @Parameter("generate_native_code")
    private boolean generateNativeCode = false;
}
