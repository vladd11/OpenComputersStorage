dofile("/home/utils.lua")

local fs = require("filesystem")
local text = require("text")

function serializeTable(val, name, skipnewlines, depth)
    skipnewlines = skipnewlines or false
    depth = depth or 0

    local tmp = string.rep(" ", depth)

    if name then
        tmp = tmp .. name .. " = "
    end

    if type(val) == "table" then
        tmp = tmp .. "{" .. (not skipnewlines and "\n" or "")

        for k, v in pairs(val) do
            tmp = tmp .. serializeTable(v, k, skipnewlines, depth + 1) .. "," .. (not skipnewlines and "\n" or "")
        end

        tmp = tmp .. string.rep(" ", depth) .. "}"
    elseif type(val) == "number" then
        tmp = tmp .. tostring(val)
    elseif type(val) == "string" then
        tmp = tmp .. string.format("%q", val)
    elseif type(val) == "boolean" then
        tmp = tmp .. (val and "true" or "false")
    else
        tmp = tmp .. "\"[inserializeable datatype:" .. type(val) .. "]\""
    end

    return tmp
end

function findPlaceToStore(amount)
    if amount == 0 then
        return {}
    end
    list = fs.list("/home/chests")

    local places = {}
    for i in list do
        f = io.open("/home/chests/" .. tostring(i), "r")
        local array = buildArray(f:lines())
        local firstLine = text.tokenize(array[1])
        local chest = { tonumber(firstLine[1]), tonumber(firstLine[2]), tonumber(firstLine[3]), tonumber(firstLine[4]), tonumber(i) }
        for n, j in ipairs(array) do
            if j == "minecraft:air" then
                table.insert(places, { chest, n - 1 }) -- first line of file is position and side
                if (#places == amount) then
                    return places
                end
            end
        end
    end
end
--for j in line_arr do
--  print(split(j, " ")[1])
--end