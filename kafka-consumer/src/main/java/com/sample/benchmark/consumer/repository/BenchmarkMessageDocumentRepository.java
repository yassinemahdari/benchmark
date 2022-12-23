package com.sample.benchmark.consumer.repository;

import com.sample.benchmark.consumer.document.BenchmarkMessageDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenchmarkMessageDocumentRepository extends ElasticsearchRepository<BenchmarkMessageDocument, String> {
}
