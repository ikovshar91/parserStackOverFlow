package main;

import json.Result;
import json.Root;
import json.Stats;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Repository {

    public static CompletableFuture<Result<Stats>> getStatsForTag(String tag, Executor executor){
    return CompletableFuture.supplyAsync(() -> {
        Result<Root> rootResult = StackOverFlowApi.getStackOverFlow(tag);
        Root root = rootResult.result;
    if(root == null){
        return new Result<>(rootResult.exception);
    }
    Stats stats = new Stats();
    stats.tag = tag;
    stats.total = root.items.size();
    stats.answered = (int) root.items.stream().filter(item -> item.is_answered).count();
    return new Result<>(stats);
    }, executor);
    }

    public static CompletableFuture<List<Result<Stats>>> getStatsForTags(String [] tags, Executor executor){
        List<CompletableFuture<Result<Stats>>> futures = Arrays.stream(tags)
                .map(tag -> Repository.getStatsForTag(tag, executor))
                .collect(Collectors.toList());

        return FutureUtils.sequence(futures);
    }

}
