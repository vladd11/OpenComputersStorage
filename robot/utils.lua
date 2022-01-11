function buildArray(...)
    local arr = {}
    for v in ... do
        arr[#arr + 1] = v
    end
    return arr
end