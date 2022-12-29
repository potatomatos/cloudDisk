package cn.cxnxs.pan.command;


import cn.cxnxs.pan.core.ElfinderContext;

public interface ElfinderCommand {

    void execute(ElfinderContext context) throws Exception;

}
