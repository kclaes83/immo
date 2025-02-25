import { TodoItem } from "../models";
import { RestService } from "./restService";

export class ItemService extends RestService<TodoItem> {
  public constructor(baseUrl: string, baseRoute: string) {
    super(baseUrl, baseRoute);
  }
}
