package com.sample.benchmark.consumer.repository;

import com.sample.benchmark.consumer.document.BenchmarkMessageDocument;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenchmarkMessageReactiveRepository extends ReactiveElasticsearchRepository<BenchmarkMessageDocument, String> {
}
