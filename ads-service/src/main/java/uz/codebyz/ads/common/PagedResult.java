package uz.codebyz.ads.common;

import java.util.List;

public record PagedResult<T>(long total, List<T> items) {}
