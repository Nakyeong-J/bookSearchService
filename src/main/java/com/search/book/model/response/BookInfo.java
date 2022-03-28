package com.search.book.model.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BookInfo {

    private final String platform = "kakao";

    private MetaInfo meta;

    private List<BookDetail> documents;
}
