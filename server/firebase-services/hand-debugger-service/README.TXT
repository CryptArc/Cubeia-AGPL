This is a Firebase service for debugging hands. It embeds a Jetty on port 19091.

If you are playing on table 6, the URL for looking at hand histories would be:
http://localhost:19091/table.html?tableid=6

It leaks memory (it stores stuff in maps that are never cleared), so do not use it in a real production system.