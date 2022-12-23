package com.sample.benchmark.consumer.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Builder
@Document(indexName = "benchmark_message_index")
public class BenchmarkMessageDocument {
    @Id
    private String messageId;
    private String message;
}
