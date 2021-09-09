package main;

import json.Result;
import json.Root;
import json.Stats;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Repository {

    public static CompletableFuture<Result<Stats>> getStatsForTag(String tag){
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
    });
    }

    public static CompletableFuture<List<Result<Stats>>> getStatsForTags(String [] tags){
        List<CompletableFuture<Result<Stats>>> futures = Arrays.stream(tags)
                .map(Repository::getStatsForTag)
                .collect(Collectors.toList());

        return FutureUtils.sequence(futures);
    }

    public static Result<List<Stats>> getStats(String [] tags){
        List<Result<Stats>> statsResult = null;
        try {
            statsResult = Repository.getStatsForTags(tags).get();

        } catch (InterruptedException | ExecutionException e){
            return new Result<>(e);
        }

        Optional<Exception> error = statsResult.stream()
                .filter(r -> r.exception != null)
                .map(r -> r.exception)
                .findFirst();
        if (error.isPresent()){
            return new Result<>(error.get());
        } else {
            LinkedList<Stats> stats = new LinkedList<>();

            statsResult.stream()
                    .filter(r -> r.result != null)
                    .map(r -> r.result)
                    .forEach(stats::add);

            return new Result<>(stats);
        }
    }
}
