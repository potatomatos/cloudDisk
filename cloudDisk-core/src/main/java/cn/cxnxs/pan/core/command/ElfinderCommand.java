package cn.cxnxs.pan.core.command;


import cn.cxnxs.pan.core.core.ElfinderContext;

public interface ElfinderCommand {

    void execute(ElfinderContext context) throws Exception;

}
