import { TodoList } from "../models";
import { RestService } from "./restService";

export class ListService extends RestService<TodoList> {
  public constructor(baseUrl: string, baseRoute: string) {
    super(baseUrl, baseRoute);
  }
}
